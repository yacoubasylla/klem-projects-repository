package com.klem.billing.application.port;

import com.klem.billing.domain.model.IntegrationMode;
import com.klem.billing.domain.model.PaymentAggregator;
import com.klem.billing.domain.model.PaymentOperator;

/**
 * Contrat unique implémenté par les deux familles d'intégration — API opérateur directe et
 * agrégateur (Adapter/Strategy). Le cœur du service ({@code TransactionService}) ne connaît que
 * cette interface, jamais les détails d'un opérateur ou agrégateur particulier — voir
 * shared_architecture/billing_&_payments/specifications_techniques.md §4/§7 pour la justification
 * de ce découpage (permettre d'ajouter l'API directe sans retoucher le cœur déjà validé sur
 * l'agrégateur).
 */
public interface PaymentProvider {

    PaymentOperator operator();

    /** Nul pour un provider DIRECT_API. */
    PaymentAggregator aggregator();

    IntegrationMode integrationMode();

    PaymentInitiationResult initiate(PaymentInitiationCommand command);

    /**
     * @throws com.klem.billing.domain.exception.InvalidWebhookSignatureException si la signature
     *         du callback (HMAC ou schéma propre à l'opérateur/agrégateur) est invalide.
     */
    void verifySignature(WebhookCallback callback);

    WebhookParseResult parseCallback(WebhookCallback callback);

    RefundResult initiateRefund(RefundCommand command);
}
