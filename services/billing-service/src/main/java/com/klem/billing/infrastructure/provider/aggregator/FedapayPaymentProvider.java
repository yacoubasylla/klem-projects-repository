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
 * Jalon 2 (V1, specifications_techniques.md §8). Contrat de champs à confirmer contre la
 * documentation API Fedapay du compte marchand KLEM avant mise en production.
 */
@Component
public class FedapayPaymentProvider implements PaymentProvider {

    private static final String SIGNATURE_HEADER = "x-fedapay-signature";

    private final RestClient restClient;
    private final AggregatorProperties.Provider config;

    public FedapayPaymentProvider(RestClient.Builder restClientBuilder, AggregatorProperties aggregatorProperties) {
        this.config = aggregatorProperties.getFedapay();
        this.restClient = restClientBuilder.baseUrl(config.getBaseUrl()).build();
    }

    @Override
    public PaymentOperator operator() {
        return null;
    }

    @Override
    public PaymentAggregator aggregator() {
        return PaymentAggregator.FEDAPAY;
    }

    @Override
    public IntegrationMode integrationMode() {
        return IntegrationMode.AGGREGATOR;
    }

    @Override
    public PaymentInitiationResult initiate(PaymentInitiationCommand command) {
        Map<String, Object> requestBody = Map.of(
                "reference", command.idempotencyKey(),
                "amount", command.amount(),
                "currency", Map.of("iso", command.currency()),
                "customer", Map.of("reference", command.payerReference() != null ? command.payerReference() : "anonymous")
        );

        Map<String, Object> response = restClient.post()
                .uri("/v1/transactions")
                .header("Authorization", "Bearer " + config.getApiKey())
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        String redirectUrl = response != null ? (String) response.get("payment_url") : null;
        return new PaymentInitiationResult(null, redirectUrl);
    }

    @Override
    public void verifySignature(WebhookCallback callback) {
        String received = callback.header(SIGNATURE_HEADER);
        if (!HmacSignatureVerifier.isValid(callback.rawBody(), received, config.getApiSecret())) {
            throw new InvalidWebhookSignatureException("Fedapay");
        }
    }

    @Override
    public WebhookParseResult parseCallback(WebhookCallback callback) {
        Map<String, Object> payload = new JacksonJsonParser().parseMap(callback.rawBody());
        String idempotencyKey = (String) payload.get("reference");
        String operatorTxId = (String) payload.getOrDefault("id", idempotencyKey);
        String rawStatus = String.valueOf(payload.get("status"));
        TransactionStatus status = "approved".equalsIgnoreCase(rawStatus)
                ? TransactionStatus.CONFIRMED
                : TransactionStatus.FAILED;
        return new WebhookParseResult(idempotencyKey, operatorTxId, status);
    }

    @Override
    public RefundResult initiateRefund(RefundCommand command) {
        return new RefundResult(command.operatorTxId(), false);
    }
}
