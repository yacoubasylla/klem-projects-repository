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
 * Agrégateur retenu pour le Jalon 1 (MVP, specifications_techniques.md §8) — couvre Wave, Orange
 * Money, MTN Mobile Money, Moov Money via la page de paiement hébergée CinetPay, ce qui place cette
 * intégration en scope PCI-DSS SAQ A (§7 : aucune donnée de paiement sensible ne transite par KLEM).
 * Le contrat exact des champs CinetPay (noms de payload, en-tête de signature) est à valider contre
 * la documentation API CinetPay du compte marchand KLEM avant mise en production — cette classe fixe
 * la forme (endpoint d'initiation, vérification de signature côté callback, mapping vers la machine
 * à états) attendue par {@link com.klem.billing.application.service.TransactionService}.
 */
@Component
public class CinetPayPaymentProvider implements PaymentProvider {

    private static final String SIGNATURE_HEADER = "x-token";

    private final RestClient restClient;
    private final AggregatorProperties.Provider config;

    public CinetPayPaymentProvider(RestClient.Builder restClientBuilder, AggregatorProperties aggregatorProperties) {
        this.config = aggregatorProperties.getCinetpay();
        this.restClient = restClientBuilder.baseUrl(config.getBaseUrl()).build();
    }

    @Override
    public PaymentOperator operator() {
        // Un provider agrégateur ne représente pas un opérateur unique : le payeur choisit son
        // canal (Wave/Orange/MTN/Moov) sur la page hébergée CinetPay elle-même. L'opérateur de la
        // transaction est fourni par l'appelant (TransactionService#initiateViaAggregator), pas
        // déduit du provider — voir PaymentProviderRegistry, qui n'indexe jamais les providers
        // agrégateur par operator().
        return null;
    }

    @Override
    public PaymentAggregator aggregator() {
        return PaymentAggregator.CINETPAY;
    }

    @Override
    public IntegrationMode integrationMode() {
        return IntegrationMode.AGGREGATOR;
    }

    @Override
    public PaymentInitiationResult initiate(PaymentInitiationCommand command) {
        Map<String, Object> requestBody = Map.of(
                "apikey", config.getApiKey(),
                "site_id", config.getSiteId(),
                "transaction_id", command.idempotencyKey(),
                "amount", command.amount(),
                "currency", command.currency(),
                "customer_id", command.payerReference() != null ? command.payerReference() : "anonymous"
        );

        Map<String, Object> response = restClient.post()
                .uri("/v2/payment")
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        Object data = response != null ? response.get("data") : null;
        String redirectUrl = data instanceof Map<?, ?> dataMap ? (String) dataMap.get("payment_url") : null;
        return new PaymentInitiationResult(null, redirectUrl);
    }

    @Override
    public void verifySignature(WebhookCallback callback) {
        String received = callback.header(SIGNATURE_HEADER);
        if (!HmacSignatureVerifier.isValid(callback.rawBody(), received, config.getApiSecret())) {
            throw new InvalidWebhookSignatureException("CinetPay");
        }
    }

    @Override
    public WebhookParseResult parseCallback(WebhookCallback callback) {
        // Champs cpm_trans_id / cpm_trans_status attendus du payload CinetPay réel — à confirmer
        // contre un callback CinetPay réel (compte sandbox) avant le Jalon 1.
        Map<String, Object> payload = new JacksonJsonParser().parseMap(callback.rawBody());
        String idempotencyKey = (String) payload.get("cpm_trans_id");
        String operatorTxId = (String) payload.getOrDefault("cpm_payid", idempotencyKey);
        String rawStatus = String.valueOf(payload.get("cpm_result"));
        TransactionStatus status = "00".equals(rawStatus) ? TransactionStatus.CONFIRMED : TransactionStatus.FAILED;
        return new WebhookParseResult(idempotencyKey, operatorTxId, status);
    }

    @Override
    public RefundResult initiateRefund(RefundCommand command) {
        // CinetPay ne propose pas d'API de remboursement automatisé sur tous les comptes marchands —
        // à confirmer selon l'offre souscrite ; en son absence, le remboursement reste manuel
        // (virement/Mobile Money direct) et cette méthode ne fait qu'acter la demande.
        return new RefundResult(command.operatorTxId(), false);
    }
}
