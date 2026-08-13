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
 * Jalon 2 (V1). Orange Money Web Payment API (OAuth2 client-credentials + notification signée) —
 * contrat de champs à revalider contre la documentation Orange Developer Center du compte marchand
 * KLEM avant mise en production.
 */
@Component
public class OrangeMoneyDirectPaymentProvider implements PaymentProvider {

    private static final String SIGNATURE_HEADER = "x-orange-signature";

    private final RestClient restClient;
    private final DirectOperatorProperties.Provider config;

    public OrangeMoneyDirectPaymentProvider(RestClient.Builder restClientBuilder, DirectOperatorProperties properties) {
        this.config = properties.getOrangeMoney();
        this.restClient = restClientBuilder.baseUrl(config.getBaseUrl()).build();
    }

    @Override
    public PaymentOperator operator() {
        return PaymentOperator.ORANGE_MONEY;
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
                "merchant_key", config.getMerchantCode(),
                "order_id", command.idempotencyKey(),
                "amount", command.amount(),
                "currency", command.currency(),
                "lang", "fr"
        );

        Map<String, Object> response = restClient.post()
                .uri("/omcoreapis/1.0.2/mp/init")
                .header("Authorization", "Bearer " + config.getClientSecret())
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        String operatorTxId = response != null ? (String) response.get("pay_token") : null;
        return new PaymentInitiationResult(operatorTxId, response != null ? (String) response.get("payment_url") : null);
    }

    @Override
    public void verifySignature(WebhookCallback callback) {
        String received = callback.header(SIGNATURE_HEADER);
        if (!HmacSignatureVerifier.isValid(callback.rawBody(), received, config.getClientSecret())) {
            throw new InvalidWebhookSignatureException("Orange Money");
        }
    }

    @Override
    public WebhookParseResult parseCallback(WebhookCallback callback) {
        Map<String, Object> payload = new JacksonJsonParser().parseMap(callback.rawBody());
        String idempotencyKey = (String) payload.get("order_id");
        String operatorTxId = (String) payload.getOrDefault("txnid", idempotencyKey);
        String rawStatus = String.valueOf(payload.get("status"));
        TransactionStatus status = "SUCCESS".equalsIgnoreCase(rawStatus)
                ? TransactionStatus.CONFIRMED
                : TransactionStatus.FAILED;
        return new WebhookParseResult(idempotencyKey, operatorTxId, status);
    }

    @Override
    public RefundResult initiateRefund(RefundCommand command) {
        // Orange Money ne propose pas d'API de remboursement automatisé sur tous les marchés —
        // remboursement manuel en son absence, cette méthode acte seulement la demande.
        return new RefundResult(command.operatorTxId(), false);
    }
}
