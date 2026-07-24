package com.klem.cantine.paiement.service;

import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.eleve.entity.StatutAcces;
import com.klem.cantine.eleve.repository.EleveRepository;
import com.klem.cantine.etablissement.entity.Classe;
import com.klem.cantine.etablissement.entity.Etablissement;
import com.klem.cantine.paiement.config.PaiementProperties;
import com.klem.cantine.paiement.dto.ModifierPaiementRequestDTO;
import com.klem.cantine.paiement.entity.OperateurMobileMoney;
import com.klem.cantine.paiement.entity.StatutPaiement;
import com.klem.cantine.paiement.entity.TransactionPaiement;
import com.klem.cantine.paiement.repository.TransactionPaiementRepository;
import com.klem.cantine.parent.repository.ParentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaiementServiceTest {

    @Mock private TransactionPaiementRepository transactionRepository;
    @Mock private EleveRepository eleveRepository;
    @Mock private ParentRepository parentRepository;
    @Mock private WebhookService webhookService;

    private final PaiementProperties paiementProperties = new PaiementProperties();

    private PaiementService paiementService;

    @BeforeEach
    void setUp() {
        paiementService = new PaiementService(
                transactionRepository, eleveRepository, parentRepository,
                paiementProperties, webhookService);
    }

    private Eleve eleve(Long id) {
        Etablissement etab = Etablissement.builder().id(1L).nom("École B").build();
        Classe classe = Classe.builder().id(1L).libelle("5ème B").anneeScolaire("2025-2026").build();
        return Eleve.builder()
                .id(id)
                .etablissement(etab)
                .classe(classe)
                .matricule("MAT-P0" + id)
                .nom("Kone")
                .prenom("Awa")
                .qrCodeToken(UUID.randomUUID())
                .statutAcces(StatutAcces.EN_ATTENTE_PAIEMENT)
                .parentNom("Kone Parent")
                .parentTelephone("0500000000")
                .build();
    }

    private TransactionPaiement transaction(StatutPaiement statut) {
        return TransactionPaiement.builder()
                .id(1L)
                .eleve(eleve(1L))
                .referenceInterne("REF-001")
                .operateur(OperateurMobileMoney.ORANGE_MONEY)
                .montant(BigDecimal.valueOf(5000))
                .statut(statut)
                .build();
    }

    @Test
    void modifier_passageVersAccepte_appliqueLesEffetsDuPaiementAccepte() {
        TransactionPaiement tx = transaction(StatutPaiement.EN_ATTENTE);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));
        when(transactionRepository.save(any())).thenReturn(tx);

        paiementService.modifier(1L, new ModifierPaiementRequestDTO(
                StatutPaiement.ACCEPTE, null, null, null));

        verify(webhookService).appliquerPaiementAccepte(tx);
    }

    @Test
    void modifier_dejaAccepte_neReappliquePasLesEffets() {
        TransactionPaiement tx = transaction(StatutPaiement.ACCEPTE);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));
        when(transactionRepository.save(any())).thenReturn(tx);

        // Une modification annexe (ex. correction du montant) sans changement de statut
        paiementService.modifier(1L, new ModifierPaiementRequestDTO(
                StatutPaiement.ACCEPTE, BigDecimal.valueOf(6000), null, null));

        verify(webhookService, never()).appliquerPaiementAccepte(any());
    }

    @Test
    void modifier_versRefuse_neDeclenchePasLesEffetsDuPaiementAccepte() {
        TransactionPaiement tx = transaction(StatutPaiement.EN_ATTENTE);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));
        when(transactionRepository.save(any())).thenReturn(tx);

        paiementService.modifier(1L, new ModifierPaiementRequestDTO(
                StatutPaiement.REFUSE, null, null, null));

        verify(webhookService, never()).appliquerPaiementAccepte(any());
    }
}
