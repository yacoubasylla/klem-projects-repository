package com.klem.cantine.paiement.service;

import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.eleve.entity.StatutAcces;
import com.klem.cantine.eleve.repository.EleveRepository;
import com.klem.cantine.etablissement.entity.Classe;
import com.klem.cantine.etablissement.entity.Etablissement;
import com.klem.cantine.paiement.entity.StatutPaiement;
import com.klem.cantine.paiement.entity.TransactionPaiement;
import com.klem.cantine.paiement.repository.TransactionPaiementRepository;
import com.klem.cantine.paiement.strategy.PaymentStrategy;
import com.klem.cantine.paiement.strategy.dto.PaymentRequestDto;
import com.klem.cantine.paiement.strategy.dto.PaymentResponseDto;
import com.klem.cantine.paiement.strategy.dto.WebhookPayloadDto;
import com.klem.cantine.paiement.strategy.enums.PaymentProviderType;
import com.klem.cantine.paiement.strategy.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CanteenPaymentServiceImplTest {

    @Mock private TransactionPaiementRepository transactionRepository;
    @Mock private EleveRepository eleveRepository;
    @Mock private PaymentStrategyFactory strategyFactory;
    @Mock private WebhookService webhookService;
    @Mock private PaymentStrategy strategy;

    private CanteenPaymentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CanteenPaymentServiceImpl(transactionRepository, eleveRepository, strategyFactory, webhookService);
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
                .referenceInterne("REF-CC-001")
                .montant(BigDecimal.valueOf(5000))
                .statut(statut)
                .build();
    }

    @Test
    void initiatePayment_persisteEnAttenteEtDelegueALaStrategie() {
        var request = PaymentRequestDto.builder()
                .studentId(1L)
                .orderId("REF-CC-001")
                .amount(BigDecimal.valueOf(5000))
                .customerPhoneNumber("0500000000")
                .provider(PaymentProviderType.ORANGE_MONEY_CI)
                .build();

        when(eleveRepository.findByIdActive(1L)).thenReturn(Optional.of(eleve(1L)));
        when(strategyFactory.getStrategy(PaymentProviderType.ORANGE_MONEY_CI)).thenReturn(strategy);
        when(strategy.getProviderType()).thenReturn(PaymentProviderType.ORANGE_MONEY_CI);
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(strategy.initiatePayment(request)).thenReturn(PaymentResponseDto.builder()
                .transactionReference("REF-CC-001")
                .providerTransactionId("PAY-TOKEN-1")
                .paymentUrl("https://payment.example/checkout")
                .status(PaymentStatus.INITIATED)
                .build());

        PaymentResponseDto response = service.initiatePayment(request);

        assertThat(response.paymentUrl()).isEqualTo("https://payment.example/checkout");
        verify(transactionRepository, org.mockito.Mockito.times(2)).save(any());
    }

    @Test
    void initiatePayment_eleveIntrouvable_leveUneException() {
        var request = PaymentRequestDto.builder()
                .studentId(99L)
                .orderId("REF-CC-404")
                .amount(BigDecimal.valueOf(5000))
                .customerPhoneNumber("0500000000")
                .build();

        when(eleveRepository.findByIdActive(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.initiatePayment(request))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void confirmWebhook_signatureInvalide_rejetteSansAppliquerLesEffets() {
        WebhookPayloadDto payload = WebhookPayloadDto.builder()
                .provider(PaymentProviderType.CINETPAY)
                .rawBody("{}")
                .fields(Map.of())
                .build();
        when(strategyFactory.getStrategy(PaymentProviderType.CINETPAY)).thenReturn(strategy);
        when(strategy.validateWebhookSignature(payload)).thenReturn(false);

        assertThatThrownBy(() -> service.confirmWebhook(PaymentProviderType.CINETPAY, payload))
                .isInstanceOf(SecurityException.class);
        verify(webhookService, never()).appliquerPaiementAccepte(any());
    }

    @Test
    void confirmWebhook_succes_appliqueLesEffetsDuPaiementAccepte() {
        WebhookPayloadDto payload = WebhookPayloadDto.builder()
                .provider(PaymentProviderType.CINETPAY)
                .rawBody("{\"cpm_trans_id\":\"REF-CC-001\"}")
                .fields(Map.of("cpm_trans_id", "REF-CC-001"))
                .build();
        TransactionPaiement tx = transaction(StatutPaiement.EN_ATTENTE);

        when(strategyFactory.getStrategy(PaymentProviderType.CINETPAY)).thenReturn(strategy);
        when(strategy.validateWebhookSignature(payload)).thenReturn(true);
        when(strategy.handleWebhook(payload)).thenReturn(PaymentResponseDto.builder()
                .transactionReference("REF-CC-001")
                .status(PaymentStatus.SUCCESS)
                .build());
        when(transactionRepository.findByReferenceInterne("REF-CC-001")).thenReturn(Optional.of(tx));
        when(transactionRepository.save(any())).thenReturn(tx);

        service.confirmWebhook(PaymentProviderType.CINETPAY, payload);

        verify(webhookService).appliquerPaiementAccepte(tx);
    }
}
