package com.klem.cantine.paiement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.eleve.entity.StatutAcces;
import com.klem.cantine.eleve.repository.EleveRepository;
import com.klem.cantine.etablissement.entity.Classe;
import com.klem.cantine.etablissement.entity.Etablissement;
import com.klem.cantine.notification.NotificationService;
import com.klem.cantine.paiement.config.PaiementProperties;
import com.klem.cantine.paiement.dto.WebhookCinetPayDTO;
import com.klem.cantine.paiement.entity.OperateurMobileMoney;
import com.klem.cantine.paiement.entity.StatutPaiement;
import com.klem.cantine.paiement.entity.TransactionPaiement;
import com.klem.cantine.paiement.repository.TransactionPaiementRepository;
import com.klem.cantine.parametrage.service.ConfigurationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock private TransactionPaiementRepository transactionRepository;
    @Mock private EleveRepository eleveRepository;
    @Mock private ObjectMapper objectMapper;
    @Mock private NotificationService notificationService;
    @Mock private ConfigurationService configurationService;

    // Instance réelle (verifySignature = false par défaut)
    private final PaiementProperties paiementProperties = new PaiementProperties();

    private WebhookService webhookService;

    @BeforeEach
    void setUp() {
        webhookService = new WebhookService(
                transactionRepository, eleveRepository, paiementProperties,
                objectMapper, notificationService, configurationService);
    }

    // ── Helpers ───────────────────────────────────────────────

    private Eleve eleve(Long id, StatutAcces statut) {
        Etablissement etab = Etablissement.builder().id(1L).nom("École B").build();
        Classe classe = Classe.builder().id(1L).libelle("5ème B").anneeScolaire("2025-2026").build();
        return Eleve.builder()
                .id(id)
                .etablissement(etab)
                .classe(classe)
                .matricule("MAT-W0" + id)
                .nom("Diallo")
                .prenom("Sekou")
                .qrCodeToken(UUID.randomUUID())
                .statutAcces(statut)
                .parentNom("Diallo Parent")
                .parentTelephone("0500000000")
                .build();
    }

    private TransactionPaiement transaction(String reference, Eleve eleve) {
        return TransactionPaiement.builder()
                .id(1L)
                .eleve(eleve)
                .referenceInterne(reference)
                .operateur(OperateurMobileMoney.ORANGE_MONEY)
                .montant(BigDecimal.valueOf(5000))
                .statut(StatutPaiement.EN_ATTENTE)
                .dateCreation(java.time.LocalDateTime.now())
                .build();
    }

    private WebhookCinetPayDTO webhookAccepte(String reference) {
        return new WebhookCinetPayDTO(
                "site_test", reference, "5000", "XOF",
                "00", "ACCEPTED", "PAYID-001", "0700000000", "sig");
    }

    private WebhookCinetPayDTO webhookRefuse(String reference) {
        return new WebhookCinetPayDTO(
                "site_test", reference, "5000", "XOF",
                "01", "REFUSED", null, "0700000000", "sig");
    }

    // ── Tests ─────────────────────────────────────────────────

    @Test
    void traiterCinetPay_accepte_metAJourTransactionEtPasseEleveAutorise() {
        String ref = "REF-ACCEPTE-001";
        Eleve eleve = eleve(1L, StatutAcces.EN_ATTENTE_PAIEMENT);
        TransactionPaiement tx = transaction(ref, eleve);

        when(transactionRepository.findByReferenceInterne(ref)).thenReturn(Optional.of(tx));
        when(transactionRepository.save(any())).thenReturn(tx);
        when(eleveRepository.save(any())).thenReturn(eleve);

        webhookService.traiterCinetPay(webhookAccepte(ref), "{\"cpm_result\":\"00\"}");

        // Transaction : statut ACCEPTE
        ArgumentCaptor<TransactionPaiement> txCaptor = ArgumentCaptor.forClass(TransactionPaiement.class);
        verify(transactionRepository).save(txCaptor.capture());
        assertThat(txCaptor.getValue().getStatut()).isEqualTo(StatutPaiement.ACCEPTE);

        // Élève : statut AUTORISE
        ArgumentCaptor<Eleve> eleveCaptor = ArgumentCaptor.forClass(Eleve.class);
        verify(eleveRepository).save(eleveCaptor.capture());
        assertThat(eleveCaptor.getValue().getStatutAcces()).isEqualTo(StatutAcces.AUTORISE);
    }

    @Test
    void traiterCinetPay_refuse_neModifiePasStatutEleve() {
        String ref = "REF-REFUSE-001";
        Eleve eleve = eleve(2L, StatutAcces.EN_ATTENTE_PAIEMENT);
        TransactionPaiement tx = transaction(ref, eleve);

        when(transactionRepository.findByReferenceInterne(ref)).thenReturn(Optional.of(tx));
        when(transactionRepository.save(any())).thenReturn(tx);

        webhookService.traiterCinetPay(webhookRefuse(ref), "{\"cpm_result\":\"01\"}");

        // Transaction : statut REFUSE
        ArgumentCaptor<TransactionPaiement> txCaptor = ArgumentCaptor.forClass(TransactionPaiement.class);
        verify(transactionRepository).save(txCaptor.capture());
        assertThat(txCaptor.getValue().getStatut()).isEqualTo(StatutPaiement.REFUSE);

        // Élève : aucune modification
        verify(eleveRepository, never()).save(any());
    }

    @Test
    void traiterCinetPay_neLevePassException_quandTransactionIntrouvable() {
        when(transactionRepository.findByReferenceInterne("REF-INCONNUE"))
                .thenReturn(Optional.empty());

        // Le try-catch interne absorbe l'erreur — aucune exception ne remonte
        webhookService.traiterCinetPay(webhookAccepte("REF-INCONNUE"), "{}");

        verify(transactionRepository, never()).save(any());
        verify(eleveRepository, never()).save(any());
    }
}
