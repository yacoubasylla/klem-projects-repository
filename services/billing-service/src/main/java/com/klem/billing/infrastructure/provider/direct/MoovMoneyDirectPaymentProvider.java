package com.klem.billing.infrastructure.provider.direct;

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
import com.klem.billing.infrastructure.config.DirectOperatorProperties;
import com.klem.billing.infrastructure.provider.HmacSignatureVerifier;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Jalon 2 (V1). L'API Moov Money varie par marché (Flooz vs Moov Money selon le pays) — contrat de
 * champs à confirmer précisément avec l'opérateur local avant mise en production ; forme générique
 * ci-dessous alignée sur les autres providers directs.
 */
@Component
public class MoovMoneyDirectPaymentProvider implements PaymentProvider {

    private static final String SIGNATURE_HEADER = "x-moov-signature";

    private final RestClient restClient;
    private final DirectOperatorProperties.Provider config;

    public MoovMoneyDirectPaymentProvider(RestClient.Builder restClientBuilder, DirectOperatorProperties properties) {
        this.config = properties.getMoovMoney();
        this.restClient = restClientBuilder.baseUrl(config.getBaseUrl()).build();
    }

    @Override
    public PaymentOperator operator() {
        return PaymentOperator.MOOV_MONEY;
    }

    @Override
    public PaymentAggregator aggregator() {
        return null;
    }

    @Override
    public IntegrationMode integrationMode() {
        return IntegrationMode.DIRECT_API;
    }

    @Override
    public PaymentInitiationResult initiate(PaymentInitiationCommand command) {
        Map<String, Object> requestBody = Map.of(
                "merchantCode", config.getMerchantCode(),
                "reference", command.idempotencyKey(),
                "amount", command.amount(),
                "currency", command.currency(),
                "msisdn", command.payerReference()
        );

        Map<String, Object> response = restClient.post()
                .uri("/v1/payments")
                .header("Authorization", "Bearer " + config.getClientSecret())
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        String operatorTxId = response != null ? (String) response.get("transactionId") : null;
        return new PaymentInitiationResult(operatorTxId, null);
    }

    @Override
    public void verifySignature(WebhookCallback callback) {
        String received = callback.header(SIGNATURE_HEADER);
        if (!HmacSignatureVerifier.isValid(callback.rawBody(), received, config.getClientSecret())) {
            throw new InvalidWebhookSignatureException("Moov Money");
        }
    }

    @Override
    public WebhookParseResult parseCallback(WebhookCallback callback) {
        Map<String, Object> payload = new JacksonJsonParser().parseMap(callback.rawBody());
        String idempotencyKey = (String) payload.get("reference");
        String operatorTxId = (String) payload.getOrDefault("transactionId", idempotencyKey);
        String rawStatus = String.valueOf(payload.get("status"));
        TransactionStatus status = "CONFIRMED".equalsIgnoreCase(rawStatus)
                ? TransactionStatus.CONFIRMED
                : TransactionStatus.FAILED;
        return new WebhookParseResult(idempotencyKey, operatorTxId, status);
    }

    @Override
    public RefundResult initiateRefund(RefundCommand command) {
        return new RefundResult(command.operatorTxId(), false);
    }
}
