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
 * Jalon 2 (V1, specifications_techniques.md §8). API opérateur directe — scope PCI-DSS élargi (§7),
 * réservée aux cas où l'agrégateur ne couvre pas le marché ciblé. Contrat de champs et schéma de
 * signature ("Wave-Signature" HMAC-SHA256, réel au moment de la rédaction) à revalider contre la
 * documentation API Wave for Business avant mise en production.
 */
@Component
public class WaveDirectPaymentProvider implements PaymentProvider {

    private static final String SIGNATURE_HEADER = "wave-signature";

    private final RestClient restClient;
    private final DirectOperatorProperties.Provider config;

    public WaveDirectPaymentProvider(RestClient.Builder restClientBuilder, DirectOperatorProperties properties) {
        this.config = properties.getWave();
        this.restClient = restClientBuilder.baseUrl(config.getBaseUrl()).build();
    }

    @Override
    public PaymentOperator operator() {
        return PaymentOperator.WAVE;
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
                "amount", command.amount().toPlainString(),
                "currency", command.currency(),
                "client_reference", command.idempotencyKey(),
                "error_url", "",
                "success_url", ""
        );

        Map<String, Object> response = restClient.post()
                .uri("/v1/checkout/sessions")
                .header("Authorization", "Bearer " + config.getClientSecret())
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        String operatorTxId = response != null ? (String) response.get("id") : null;
        return new PaymentInitiationResult(operatorTxId, response != null ? (String) response.get("wave_launch_url") : null);
    }

    @Override
    public void verifySignature(WebhookCallback callback) {
        String received = callback.header(SIGNATURE_HEADER);
        if (!HmacSignatureVerifier.isValid(callback.rawBody(), received, config.getClientSecret())) {
            throw new InvalidWebhookSignatureException("Wave");
        }
    }

    @Override
    public WebhookParseResult parseCallback(WebhookCallback callback) {
        Map<String, Object> payload = new JacksonJsonParser().parseMap(callback.rawBody());
        String idempotencyKey = (String) payload.get("client_reference");
        String operatorTxId = (String) payload.getOrDefault("id", idempotencyKey);
        String rawStatus = String.valueOf(payload.get("payment_status"));
        TransactionStatus status = "succeeded".equalsIgnoreCase(rawStatus)
                ? TransactionStatus.CONFIRMED
                : TransactionStatus.FAILED;
        return new WebhookParseResult(idempotencyKey, operatorTxId, status);
    }

    @Override
    public RefundResult initiateRefund(RefundCommand command) {
        restClient.post()
                .uri("/v1/checkout/sessions/{id}/refund", command.operatorTxId())
                .header("Authorization", "Bearer " + config.getClientSecret())
                .retrieve()
                .toBodilessEntity();
        return new RefundResult(command.operatorTxId(), true);
    }
}
