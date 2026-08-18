package com.klem.cantine.paiement.strategy.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klem.cantine.paiement.config.PaiementProperties;
import com.klem.cantine.paiement.strategy.PaymentStrategy;
import com.klem.cantine.paiement.strategy.dto.PaymentRequestDto;
import com.klem.cantine.paiement.strategy.dto.PaymentResponseDto;
import com.klem.cantine.paiement.strategy.dto.WebhookPayloadDto;
import com.klem.cantine.paiement.strategy.enums.PaymentProviderType;
import com.klem.cantine.paiement.strategy.enums.PaymentStatus;
import com.klem.cantine.paiement.strategy.exception.PaymentProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Intégration marchande directe Orange Money Webpayment CI.
 * <p>
 * Remplace fonctionnellement le placeholder historique {@code OrangeMoneyDirectProvider}
 * (accès marchand désormais disponible) sans toucher à ce dernier ni à
 * {@code com.klem.cantine.paiement.provider.PaymentProvider} — cette stratégie n'est
 * accessible que via le nouveau contrat {@link PaymentStrategy} /
 * {@code CanteenPaymentServiceImpl}.
 * <p>
 * Authentification OAuth2 client_credentials : le jeton d'accès est mis en cache en mémoire
 * (un seul jeton pour toute l'instance, l'API Orange étant appelée pour le compte marchand
 * global, pas par utilisateur final) et renouvelé automatiquement 30 secondes avant expiration.
 */
@Component
@Slf4j
public class OrangeMoneyPaymentStrategy implements PaymentStrategy {

    private static final Duration EXPIRY_SAFETY_MARGIN = Duration.ofSeconds(30);

    private final WebClient webClient;
    private final PaiementProperties paiementProperties;
    private final ObjectMapper objectMapper;
    private final AtomicReference<CachedToken> cachedToken = new AtomicReference<>();

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.backend-url}")
    private String backendUrl;

    public OrangeMoneyPaymentStrategy(PaiementProperties paiementProperties, ObjectMapper objectMapper) {
        this.paiementProperties = paiementProperties;
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .clientConnector(new org.springframework.http.client.reactive.ReactorClientHttpConnector(
                        reactor.netty.http.client.HttpClient.create().responseTimeout(Duration.ofSeconds(15))))
                .build();
    }

    private record CachedToken(String accessToken, Instant expiresAt) {
        boolean isValid() {
            return Instant.now().isBefore(expiresAt.minus(EXPIRY_SAFETY_MARGIN));
        }
    }

    @Override
    public PaymentProviderType getProviderType() {
        return PaymentProviderType.ORANGE_MONEY_CI;
    }

    @Override
    public PaymentResponseDto initiatePayment(PaymentRequestDto request) {
        var orangeMoney = paiementProperties.getOrangeMoney();
        String accessToken = resolveAccessToken();

        Map<String, Object> body = new HashMap<>();
        body.put("merchant_key", orangeMoney.getMerchantKey());
        body.put("currency", "XOF");
        body.put("order_id", request.orderId());
        body.put("amount", request.amount().intValue());
        body.put("return_url", Objects.requireNonNullElse(request.returnUrl(), frontendUrl + "/paiements"));
        body.put("cancel_url", Objects.requireNonNullElse(request.cancelUrl(), frontendUrl + "/paiements"));
        body.put("notif_url", backendUrl + "/api/v2/canteen-payments/webhooks/ORANGE_MONEY_CI");
        body.put("lang", "fr");
        body.put("reference", request.description() != null
                ? request.description() : "Paiement cantine — élève #" + request.studentId());

        JsonNode reponse;
        try {
            reponse = webClient.post()
                    .uri(orangeMoney.getWebpaymentUrl())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(this::parseJson)
                    .block(Duration.ofSeconds(20));
        } catch (Exception e) {
            log.error("Erreur réseau lors de l'appel à Orange Money Webpayment : {}", e.getMessage());
            throw new PaymentProviderException(
                    "Impossible de contacter le service de paiement Orange Money pour le moment.", e);
        }

        String paymentUrl = reponse.path("payment_url").asText(null);
        String payToken = reponse.path("pay_token").asText(null);
        if (paymentUrl == null || paymentUrl.isBlank()) {
            String message = reponse.path("message").asText("Réponse Orange Money invalide : URL de paiement manquante.");
            log.warn("Échec initiation Orange Money : {}", message);
            throw new PaymentProviderException(
                    "Le service de paiement Orange Money est indisponible ou mal configuré : " + message);
        }

        return PaymentResponseDto.builder()
                .transactionReference(request.orderId())
                .providerTransactionId(payToken)
                .paymentUrl(paymentUrl)
                .status(PaymentStatus.INITIATED)
                .rawResponse(objectMapper.convertValue(reponse, Map.class))
                .build();
    }

    @Override
    public PaymentResponseDto handleWebhook(WebhookPayloadDto webhookPayload) {
        Map<String, Object> fields = webhookPayload.fields();
        String orderId = String.valueOf(fields.get("order_id"));
        String status = String.valueOf(fields.getOrDefault("status", "")).toUpperCase();
        String txnId = fields.get("txnid") != null ? String.valueOf(fields.get("txnid")) : null;

        return PaymentResponseDto.builder()
                .transactionReference(orderId)
                .providerTransactionId(txnId)
                .status(mapOrangeStatus(status))
                .rawResponse(fields)
                .build();
    }

    private PaymentStatus mapOrangeStatus(String orangeStatus) {
        return switch (orangeStatus) {
            case "SUCCESS", "SUCCESSFUL" -> PaymentStatus.SUCCESS;
            case "FAILED", "FAILURE" -> PaymentStatus.FAILED;
            case "EXPIRED" -> PaymentStatus.EXPIRED;
            case "CANCELLED", "CANCELED" -> PaymentStatus.CANCELLED;
            default -> PaymentStatus.PENDING;
        };
    }

    @Override
    public PaymentResponseDto checkTransactionStatus(String transactionReference) {
        var orangeMoney = paiementProperties.getOrangeMoney();
        String accessToken = resolveAccessToken();

        Map<String, Object> body = Map.of(
                "order_id", transactionReference,
                "merchant_key", orangeMoney.getMerchantKey()
        );

        JsonNode reponse;
        try {
            reponse = webClient.post()
                    .uri(orangeMoney.getWebpaymentUrl() + "/transactionstatus")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(this::parseJson)
                    .block(Duration.ofSeconds(20));
        } catch (Exception e) {
            log.error("Erreur réseau lors de la vérification de statut Orange Money : {}", e.getMessage());
            throw new PaymentProviderException(
                    "Impossible d'interroger le service Orange Money pour le moment.", e);
        }

        return PaymentResponseDto.builder()
                .transactionReference(transactionReference)
                .providerTransactionId(reponse.path("txnid").asText(null))
                .status(mapOrangeStatus(reponse.path("status").asText("").toUpperCase()))
                .rawResponse(objectMapper.convertValue(reponse, Map.class))
                .build();
    }

    /**
     * ⚠️ Squelette — l'API Orange Money Webpayment CI ne documente pas publiquement de schéma
     * de signature HMAC pour ses notifications ({@code notif_url}), contrairement à
     * CinetPay/PayDunya. Cette méthode applique une vérification HMAC-SHA256 générique
     * (clé {@code paiement.orange-money.webhook-secret}) en attendant confirmation du schéma
     * exact auprès du support marchand Orange — même posture prudente que
     * {@code WebhookService#verifierSignaturePayDunya} : {@code verify-signature} reste à
     * {@code false} par défaut tant que non confirmé en production.
     */
    @Override
    public boolean validateWebhookSignature(WebhookPayloadDto webhookPayload) {
        var orangeMoney = paiementProperties.getOrangeMoney();
        if (!orangeMoney.isVerifySignature()) {
            return true;
        }
        String secret = orangeMoney.getWebhookSecret();
        String recu = webhookPayload.signatureHeaders() != null
                ? webhookPayload.signatureHeaders().get("X-Signature") : null;
        if (secret == null || recu == null) {
            return false;
        }
        String attendu = hmacSha256Hex(secret, webhookPayload.rawBody());
        return attendu.equalsIgnoreCase(recu);
    }

    private String hmacSha256Hex(String secret, String message) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new PaymentProviderException("Erreur calcul HMAC-SHA256 pour la signature Orange Money", e);
        }
    }

    // ── Authentification OAuth2 (client_credentials, jeton mis en cache) ───────

    private String resolveAccessToken() {
        CachedToken current = cachedToken.get();
        if (current != null && current.isValid()) {
            return current.accessToken();
        }
        return refreshAccessToken();
    }

    private synchronized String refreshAccessToken() {
        CachedToken current = cachedToken.get();
        if (current != null && current.isValid()) {
            return current.accessToken();
        }

        var orangeMoney = paiementProperties.getOrangeMoney();
        String basicAuth = Base64.getEncoder().encodeToString(
                (orangeMoney.getClientId() + ":" + orangeMoney.getClientSecret()).getBytes(StandardCharsets.UTF_8));

        JsonNode reponse;
        try {
            reponse = webClient.post()
                    .uri(orangeMoney.getAuthUrl())
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(this::parseJson)
                    .block(Duration.ofSeconds(15));
        } catch (Exception e) {
            log.error("Erreur réseau lors de l'authentification OAuth2 Orange Money : {}", e.getMessage());
            throw new PaymentProviderException(
                    "Impossible de s'authentifier auprès d'Orange Money pour le moment.", e);
        }

        String accessToken = reponse.path("access_token").asText(null);
        long expiresInSeconds = reponse.path("expires_in").asLong(3600);
        if (accessToken == null || accessToken.isBlank()) {
            throw new PaymentProviderException("Réponse OAuth2 Orange Money invalide : access_token manquant.");
        }

        CachedToken fresh = new CachedToken(accessToken, Instant.now().plusSeconds(expiresInSeconds));
        cachedToken.set(fresh);
        return accessToken;
    }

    private JsonNode parseJson(String raw) {
        try {
            return objectMapper.readTree(raw);
        } catch (Exception e) {
            throw new PaymentProviderException("Réponse Orange Money illisible (JSON invalide).", e);
        }
    }
}
