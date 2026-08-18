package com.klem.cantine.paiement.service;

import com.klem.cantine.actionlog.annotation.Traceable;
import com.klem.cantine.actionlog.entity.TypeAction;
import com.klem.cantine.eleve.repository.EleveRepository;
import com.klem.cantine.paiement.entity.OperateurMobileMoney;
import com.klem.cantine.paiement.entity.StatutPaiement;
import com.klem.cantine.paiement.entity.TransactionPaiement;
import com.klem.cantine.paiement.repository.TransactionPaiementRepository;
import com.klem.cantine.paiement.strategy.PaymentStrategy;
import com.klem.cantine.paiement.strategy.dto.PaymentRequestDto;
import com.klem.cantine.paiement.strategy.dto.PaymentResponseDto;
import com.klem.cantine.paiement.strategy.dto.WebhookPayloadDto;
import com.klem.cantine.paiement.strategy.enums.PaymentProviderType;
import com.klem.cantine.paiement.strategy.enums.PaymentStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implémentation de {@link CanteenPaymentService}.
 * <p>
 * Réutilise volontairement la table/entité historique {@link TransactionPaiement} (via
 * {@link TransactionPaiementRepository}) plutôt qu'un nouveau modèle parallèle : une seule
 * source de vérité pour les transactions de paiement, quel que soit le contrat (historique
 * {@code PaymentProvider} ou unifié {@link PaymentStrategy}) qui les a initiées.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CanteenPaymentServiceImpl implements CanteenPaymentService {

    private final TransactionPaiementRepository transactionRepository;
    private final EleveRepository eleveRepository;
    private final PaymentStrategyFactory strategyFactory;
    private final WebhookService webhookService;

    @Traceable(action = TypeAction.CREATE, entite = "TransactionPaiement")
    @Transactional
    @Override
    public PaymentResponseDto initiatePayment(PaymentRequestDto request) {
        var eleve = eleveRepository.findByIdActive(request.studentId())
                .orElseThrow(() -> new EntityNotFoundException("Élève introuvable : " + request.studentId()));

        PaymentStrategy strategy = strategyFactory.getStrategy(request.provider());

        TransactionPaiement transaction = TransactionPaiement.builder()
                .eleve(eleve)
                .referenceInterne(request.orderId())
                .operateur(toOperateurMobileMoney(strategy.getProviderType()))
                .montant(request.amount())
                .telephonePayeur(request.customerPhoneNumber())
                .statut(StatutPaiement.EN_ATTENTE)
                .build();
        transaction = transactionRepository.save(transaction);

        PaymentResponseDto response = strategy.initiatePayment(request);

        transaction.setReferencePlateforme(response.providerTransactionId());
        transactionRepository.save(transaction);

        log.info("Paiement initié (multi-provider) : ref={} provider={} montant={} XOF",
                request.orderId(), strategy.getProviderType(), request.amount());

        return response;
    }

    @Traceable(action = TypeAction.UPDATE, entite = "TransactionPaiement")
    @Transactional
    @Override
    public PaymentResponseDto confirmWebhook(PaymentProviderType provider, WebhookPayloadDto webhookPayload) {
        PaymentStrategy strategy = strategyFactory.getStrategy(provider);

        if (!strategy.validateWebhookSignature(webhookPayload)) {
            throw new SecurityException("Signature de webhook invalide pour le provider " + provider + " — rejeté");
        }

        PaymentResponseDto response = strategy.handleWebhook(webhookPayload);

        TransactionPaiement transaction = transactionRepository
                .findByReferenceInterne(response.transactionReference())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Transaction introuvable pour la référence : " + response.transactionReference()));

        StatutPaiement ancienStatut = transaction.getStatut();
        transaction.setStatut(toStatutPaiement(response.status()));
        transaction.setDateMiseAJour(LocalDateTime.now());
        transaction.setMetadonneesWebhook(webhookPayload.rawBody());
        if (response.providerTransactionId() != null) {
            transaction.setReferencePlateforme(response.providerTransactionId());
        }
        TransactionPaiement saved = transactionRepository.save(transaction);

        if (saved.getStatut() == StatutPaiement.ACCEPTE && ancienStatut != StatutPaiement.ACCEPTE) {
            webhookService.appliquerPaiementAccepte(saved);
        }

        log.info("Webhook {} confirmé : ref={} statut={}", provider, response.transactionReference(), response.status());
        return response;
    }

    @Override
    public PaymentResponseDto checkTransactionStatus(PaymentProviderType provider, String transactionReference) {
        return strategyFactory.getStrategy(provider).checkTransactionStatus(transactionReference);
    }

    /**
     * {@link PaymentProviderType} (passerelle) et {@link OperateurMobileMoney} (opérateur télécom
     * effectivement utilisé par le payeur) sont deux axes différents — un agrégateur comme
     * CinetPay accepte tous les opérateurs. Faute d'un champ operateur explicite dans
     * {@link PaymentRequestDto}, on retient le mapping direct le plus proche ; à affiner si
     * cette information devient disponible côté client (ticket de suivi à ouvrir si besoin).
     */
    private OperateurMobileMoney toOperateurMobileMoney(PaymentProviderType provider) {
        return switch (provider) {
            case ORANGE_MONEY_CI -> OperateurMobileMoney.ORANGE_MONEY;
            case MTN_MOMO_CI -> OperateurMobileMoney.MTN_MONEY;
            case WAVE_CI -> OperateurMobileMoney.WAVE;
            case CINETPAY -> OperateurMobileMoney.ORANGE_MONEY;
        };
    }

    private StatutPaiement toStatutPaiement(PaymentStatus status) {
        return switch (status) {
            case INITIATED, PENDING -> StatutPaiement.EN_ATTENTE;
            case SUCCESS -> StatutPaiement.ACCEPTE;
            case FAILED -> StatutPaiement.REFUSE;
            case CANCELLED, EXPIRED -> StatutPaiement.ANNULE;
        };
    }
}
