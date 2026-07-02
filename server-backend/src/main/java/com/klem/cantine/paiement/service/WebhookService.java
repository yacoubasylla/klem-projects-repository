package com.klem.cantine.paiement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klem.cantine.eleve.entity.StatutAcces;
import com.klem.cantine.eleve.repository.EleveRepository;
import com.klem.cantine.notification.NotificationService;
import com.klem.cantine.paiement.config.PaiementProperties;
import com.klem.cantine.paiement.dto.WebhookCinetPayDTO;
import com.klem.cantine.paiement.dto.WebhookPayDunyaDTO;
import com.klem.cantine.paiement.entity.StatutPaiement;
import com.klem.cantine.paiement.entity.TransactionPaiement;
import com.klem.cantine.paiement.repository.TransactionPaiementRepository;
import com.klem.cantine.parametrage.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final TransactionPaiementRepository transactionRepository;
    private final EleveRepository eleveRepository;
    private final PaiementProperties paiementProperties;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final ConfigurationService configurationService;

    // ── CinetPay ──────────────────────────────────────────────────────────────

    @Async
    @Transactional
    public void traiterCinetPay(WebhookCinetPayDTO dto, String rawBody) {
        try {
            if (paiementProperties.getCinetpay().isVerifySignature()) {
                verifierSignatureCinetPay(dto);
            }

            TransactionPaiement transaction = transactionRepository
                    .findByReferenceInterne(dto.cpmTransId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Transaction CinetPay introuvable : " + dto.cpmTransId()));

            appliquerResultat(transaction, dto.estAccepte(), dto.cpmPayid(), rawBody);

            log.info("Webhook CinetPay traité : ref={} résultat={}",
                    dto.cpmTransId(), dto.cpmTransStatus());

        } catch (Exception e) {
            log.error("Erreur traitement webhook CinetPay : {}", e.getMessage(), e);
        }
    }

    // ── PayDunya ──────────────────────────────────────────────────────────────

    @Async
    @Transactional
    public void traiterPayDunya(WebhookPayDunyaDTO dto, String rawBody) {
        try {
            if (paiementProperties.getPaydunya().isVerifySignature()) {
                verifierSignaturePayDunya(dto, rawBody);
            }

            TransactionPaiement transaction = transactionRepository
                    .findByReferenceInterne(dto.token())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Transaction PayDunya introuvable : " + dto.token()));

            appliquerResultat(transaction, dto.estAccepte(), null, rawBody);

            log.info("Webhook PayDunya traité : token={} statut={}",
                    dto.token(), dto.status());

        } catch (Exception e) {
            log.error("Erreur traitement webhook PayDunya : {}", e.getMessage(), e);
        }
    }

    // ── Logique commune ───────────────────────────────────────────────────────

    private void appliquerResultat(TransactionPaiement transaction, boolean accepte,
                                    String referencePlateforme, String rawBody) {
        transaction.setStatut(accepte ? StatutPaiement.ACCEPTE : StatutPaiement.REFUSE);
        transaction.setDateMiseAJour(LocalDateTime.now());
        transaction.setMetadonneesWebhook(rawBody);

        if (referencePlateforme != null) {
            transaction.setReferencePlateforme(referencePlateforme);
        }

        transactionRepository.save(transaction);

        // Mise à jour statut élève uniquement si paiement accepté
        if (accepte) {
            appliquerPaiementAccepte(transaction);
        }
    }

    /**
     * Applique à l'élève les effets d'un paiement accepté (statut AUTORISE, crédit du
     * solde en mode CREDITS, notification) — partagé entre les webhooks et la
     * confirmation manuelle d'un paiement (PaiementService.modifier).
     */
    void appliquerPaiementAccepte(TransactionPaiement transaction) {
        var eleve = transaction.getEleve();
        eleve.setStatutAcces(StatutAcces.AUTORISE);

        // Crédit du solde en mode CREDITS
        if ("CREDITS".equalsIgnoreCase(configurationService.getValeur("MODE_PAIEMENT"))) {
            BigDecimal montant = transaction.getMontant() != null
                    ? transaction.getMontant() : BigDecimal.ZERO;
            eleve.setSolde(eleve.getSolde().add(montant));
            log.info("Solde élève {} crédité de {} FCFA → nouveau solde {}",
                    eleve.getId(), montant, eleve.getSolde());
        }

        eleveRepository.save(eleve);
        log.info("Élève {} → AUTORISE après paiement accepté", eleve.getId());
        notificationService.notifierPaiementAccepte(eleve,
                transaction.getMontant() != null ? transaction.getMontant() : BigDecimal.ZERO);
    }

    // ── Vérification signature CinetPay ───────────────────────────────────────

    private void verifierSignatureCinetPay(WebhookCinetPayDTO dto) {
        String apiSecret = paiementProperties.getCinetpay().getApiSecret();
        String siteId    = paiementProperties.getCinetpay().getSiteId();
        String input = apiSecret + siteId + dto.cpmTransId() + dto.cpmAmount() + dto.cpmCurrency();
        String expected  = sha256(input);
        if (!expected.equalsIgnoreCase(dto.signature())) {
            throw new SecurityException("Signature CinetPay invalide — webhook rejeté");
        }
    }

    // ── Vérification signature PayDunya ──────────────────────────────────────

    private void verifierSignaturePayDunya(WebhookPayDunyaDTO dto, String rawBody) {
        try {
            String masterKey = paiementProperties.getPaydunya().getMasterKey();
            String expected  = sha256(masterKey + rawBody);
            // PayDunya envoie la signature dans un header X-PayDunya-Signature
            // La vérification complète nécessite l'accès au header HTTP — à implémenter dans le controller si nécessaire
            log.debug("Signature PayDunya calculée : {}", expected);
        } catch (Exception e) {
            throw new SecurityException("Signature PayDunya invalide — webhook rejeté");
        }
    }

    // ── Utilitaire SHA-256 ────────────────────────────────────────────────────

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erreur calcul SHA-256", e);
        }
    }
}
