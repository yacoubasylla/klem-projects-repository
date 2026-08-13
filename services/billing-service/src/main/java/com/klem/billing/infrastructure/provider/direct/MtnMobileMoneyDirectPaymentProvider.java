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
import java.util.UUID;

/**
 * Jalon 2 (V1). MTN MoMo Collection API (clé d'abonnement + jeton OAuth2) — contrat de champs à
 * revalider contre la documentation MTN MoMo Developer Portal du compte marchand KLEM avant mise en
 * production.
 */
@Component
public class MtnMobileMoneyDirectPaymentProvider implements PaymentProvider {

    private static final String SIGNATURE_HEADER = "x-mtn-signature";

    private final RestClient restClient;
    private final DirectOperatorProperties.Provider config;

    public MtnMobileMoneyDirectPaymentProvider(RestClient.Builder restClientBuilder, DirectOperatorProperties properties) {
        this.config = properties.getMtnMobileMoney();
        this.restClient = restClientBuilder.baseUrl(config.getBaseUrl()).build();
    }

    @Override
    public PaymentOperator operator() {
        return PaymentOperator.MTN_MOBILE_MONEY;
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
        String referenceId = UUID.randomUUID().toString();
        Map<String, Object> requestBody = Map.of(
                "amount", command.amount().toPlainString(),
                "currency", command.currency(),
                "externalId", command.idempotencyKey(),
                "payer", Map.of("partyIdType", "MSISDN", "partyId", command.payerReference())
        );

        restClient.post()
                .uri("/collection/v1_0/requesttopay")
                .header("X-Reference-Id", referenceId)
                .header("Ocp-Apim-Subscription-Key", config.getClientId())
                .header("Authorization", "Bearer " + config.getClientSecret())
                .body(requestBody)
                .retrieve()
                .toBodilessEntity();

        return new PaymentInitiationResult(referenceId, null);
    }

    @Override
    public void verifySignature(WebhookCallback callback) {
        String received = callback.header(SIGNATURE_HEADER);
        if (!HmacSignatureVerifier.isValid(callback.rawBody(), received, config.getClientSecret())) {
            throw new InvalidWebhookSignatureException("MTN Mobile Money");
        }
    }

    @Override
    public WebhookParseResult parseCallback(WebhookCallback callback) {
        Map<String, Object> payload = new JacksonJsonParser().parseMap(callback.rawBody());
        String operatorTxId = (String) payload.get("referenceId");
        String idempotencyKey = (String) payload.getOrDefault("externalId", operatorTxId);
        String rawStatus = String.valueOf(payload.get("status"));
        TransactionStatus status = "SUCCESSFUL".equalsIgnoreCase(rawStatus)
                ? TransactionStatus.CONFIRMED
                : TransactionStatus.FAILED;
        return new WebhookParseResult(idempotencyKey, operatorTxId, status);
    }

    @Override
    public RefundResult initiateRefund(RefundCommand command) {
        restClient.post()
                .uri("/disbursement/v1_0/refund")
                .header("Ocp-Apim-Subscription-Key", config.getClientId())
                .header("Authorization", "Bearer " + config.getClientSecret())
                .body(Map.of("referenceIdToRefund", command.operatorTxId(), "amount", command.amount().toPlainString()))
                .retrieve()
                .toBodilessEntity();
        return new RefundResult(command.operatorTxId(), true);
    }
}
