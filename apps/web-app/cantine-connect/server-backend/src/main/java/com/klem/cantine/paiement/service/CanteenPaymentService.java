package com.klem.cantine.paiement.service;

import com.klem.cantine.paiement.strategy.dto.PaymentRequestDto;
import com.klem.cantine.paiement.strategy.dto.PaymentResponseDto;
import com.klem.cantine.paiement.strategy.dto.WebhookPayloadDto;
import com.klem.cantine.paiement.strategy.enums.PaymentProviderType;

/**
 * Service métier façade du paiement multi-providers — point d'entrée unique pour
 * {@code CanteenPaymentController}, au-dessus de {@link PaymentStrategyFactory} et des
 * {@link com.klem.cantine.paiement.strategy.PaymentStrategy}.
 * <p>
 * Coexiste avec {@code PaiementService}/{@code /api/v1/paiements}, laissé inchangé
 * (non-régression) : les deux persistent dans la même table {@code transactions_paiement}.
 */
public interface CanteenPaymentService {

    PaymentResponseDto initiatePayment(PaymentRequestDto request);

    /** Valide la signature puis applique le résultat du webhook à la transaction concernée. */
    PaymentResponseDto confirmWebhook(PaymentProviderType provider, WebhookPayloadDto webhookPayload);

    PaymentResponseDto checkTransactionStatus(PaymentProviderType provider, String transactionReference);
}
