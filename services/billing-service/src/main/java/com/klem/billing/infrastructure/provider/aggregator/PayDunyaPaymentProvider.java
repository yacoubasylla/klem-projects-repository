package com.klem.billing.infrastructure.provider.aggregator;

import com.klem.billing.application.port.PaymentInitiationCommand;
import com.klem.billing.application.port.PaymentInitiationResult;
import com.klem.billing.application.port.PaymentProvider;
import com.klem.billing.application.port.RefundCommand;
import com.klem.billing.application.port.RefundResult;
import com.klem.billing.application.port.WebhookCallback;
import com.klem.billing.application.port.WebhookParseResult;
import com.klem.billing.domain.exception.InvalidWebhookSignatureException;
import com.klem.billing.domain.model.IntegrationMode;
import com.klem.billing.domain.model.PaymentAggregator;
import com.klem.billing.domain.model.PaymentOperator;
import com.klem.billing.domain.model.TransactionStatus;
import com.klem.billing.infrastructure.config.AggregatorProperties;
import com.klem.billing.infrastructure.provider.HmacSignatureVerifier;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Déjà en production côté `cantine_connect` (aux côtés de CinetPay). Jalon 2 (V1) pour son
 * intégration au service partagé — contrat de champs à confirmer contre la configuration réelle de
 * `cantine_connect` (`PaymentProvider.java`) plutôt que redécouvert depuis zéro.
 */
@Component
public class PayDunyaPaymentProvider implements PaymentProvider {

    private static final String SIGNATURE_HEADER = "x-paydunya-signature";

    private final RestClient restClient;
    private final AggregatorProperties.Provider config;

    public PayDunyaPaymentProvider(RestClient.Builder restClientBuilder, AggregatorProperties aggregatorProperties) {
        this.config = aggregatorProperties.getPaydunya();
        this.restClient = restClientBuilder.baseUrl(config.getBaseUrl()).build();
    }

    @Override
    public PaymentOperator operator() {
        return null;
    }

    @Override
    public PaymentAggregator aggregator() {
        return PaymentAggregator.PAYDUNYA;
    }

    @Override
    public IntegrationMode integrationMode() {
        return IntegrationMode.AGGREGATOR;
    }

    @Override
    public PaymentInitiationResult initiate(PaymentInitiationCommand command) {
        Map<String, Object> requestBody = Map.of(
                "invoice", Map.of(
                        "total_amount", command.amount(),
                        "description", "KLEM " + command.idempotencyKey()
                ),
                "custom_data", Map.of("idempotency_key", command.idempotencyKey())
        );

        Map<String, Object> response = restClient.post()
                .uri("/api/v1/checkout-invoice/create")
                .header("PAYDUNYA-MASTER-KEY", config.getApiKey())
                .header("PAYDUNYA-PRIVATE-KEY", config.getApiSecret())
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        String redirectUrl = response != null ? (String) response.get("response_text") : null;
        return new PaymentInitiationResult(null, redirectUrl);
    }

    @Override
    public void verifySignature(WebhookCallback callback) {
        String received = callback.header(SIGNATURE_HEADER);
        if (!HmacSignatureVerifier.isValid(callback.rawBody(), received, config.getApiSecret())) {
            throw new InvalidWebhookSignatureException("PayDunya");
        }
    }

    @Override
    public WebhookParseResult parseCallback(WebhookCallback callback) {
        Map<String, Object> payload = new JacksonJsonParser().parseMap(callback.rawBody());
        Map<?, ?> customData = payload.get("custom_data") instanceof Map<?, ?> m ? m : Map.of();
        String idempotencyKey = (String) customData.get("idempotency_key");
        String operatorTxId = (String) payload.getOrDefault("invoice_token", idempotencyKey);
        String rawStatus = String.valueOf(payload.get("status"));
        TransactionStatus status = "completed".equalsIgnoreCase(rawStatus)
                ? TransactionStatus.CONFIRMED
                : TransactionStatus.FAILED;
        return new WebhookParseResult(idempotencyKey, operatorTxId, status);
    }

    @Override
    public RefundResult initiateRefund(RefundCommand command) {
        return new RefundResult(command.operatorTxId(), false);
    }
}
