package com.klem.cantine.paiement.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klem.cantine.paiement.config.PaiementProperties;
import com.klem.cantine.paiement.dto.InitierPaiementRequestDTO;
import com.klem.cantine.paiement.entity.OperateurMobileMoney;
import com.klem.cantine.paiement.strategy.PaymentStrategy;
import com.klem.cantine.paiement.strategy.dto.PaymentRequestDto;
import com.klem.cantine.paiement.strategy.dto.PaymentResponseDto;
import com.klem.cantine.paiement.strategy.dto.WebhookPayloadDto;
import com.klem.cantine.paiement.strategy.enums.PaymentProviderType;
import com.klem.cantine.paiement.strategy.enums.PaymentStatus;
import com.klem.cantine.paiement.strategy.exception.PaymentProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Intégration réelle CinetPay — appelle l'API "Standard Payment"
 * (POST /v2/payment) pour obtenir une URL de checkout réelle.
 * Doc officielle : https://docs.cinetpay.com/api/1.0-en/checkout/initialisation
 *
 * ⚠️ Sans clés réelles (CINETPAY_API_KEY/SITE_ID en environnement), l'appel
 * échoue proprement côté CinetPay (identifiants invalides) — traduit en
 * IllegalStateException avec message clair, pas de crash de l'application.
 * <p>
 * Implémente en plus {@link PaymentStrategy} (contrat unifié multi-providers) : les méthodes
 * ci-dessus ({@code getCode}/{@code initierPaiement}) ne sont pas modifiées, les méthodes du
 * nouveau contrat sont ajoutées à la suite et délèguent au même appel HTTP existant.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CinetPayProvider implements PaymentProvider, PaymentStrategy {

    private static final String ENDPOINT = "https://api-checkout.cinetpay.com/v2/payment";

    private final PaiementProperties paiementProperties;
    private final ObjectMapper objectMapper;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.backend-url}")
    private String backendUrl;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public String getCode() {
        return "CINETPAY";
    }

    @Override
    public String initierPaiement(String referenceInterne, InitierPaiementRequestDTO dto) {
        var cinetpay = paiementProperties.getCinetpay();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("apikey", cinetpay.getApiKey());
        body.put("site_id", cinetpay.getSiteId());
        body.put("transaction_id", referenceInterne);
        body.put("amount", dto.montant().setScale(0, RoundingMode.HALF_UP).longValue());
        body.put("currency", "XOF");
        body.put("description", "Paiement cantine — élève #" + dto.eleveId());
        body.put("customer_phone_number", dto.telephonePayeur());
        body.put("notify_url", backendUrl + "/api/v1/webhooks/cinetpay");
        body.put("return_url", frontendUrl + "/paiements");
        body.put("channels", "ALL");
        body.put("lang", "fr");

        try {
            String json = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode racine = objectMapper.readTree(response.body());
            String code = racine.path("code").asText();

            if (!"201".equals(code)) {
                String message = racine.path("message").asText("Erreur inconnue");
                String description = racine.path("description").asText("");
                log.warn("Échec initiation CinetPay (code={}) : {} — {}", code, message, description);
                throw new IllegalStateException(
                        "Le service de paiement CinetPay est indisponible ou mal configuré : " + message);
            }

            String paymentUrl = racine.path("data").path("payment_url").asText(null);
            if (paymentUrl == null || paymentUrl.isBlank()) {
                throw new IllegalStateException("Réponse CinetPay invalide : URL de paiement manquante.");
            }
            return paymentUrl;

        } catch (java.io.IOException | InterruptedException e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            log.error("Erreur réseau lors de l'appel à CinetPay : {}", e.getMessage());
            throw new IllegalStateException(
                    "Impossible de contacter le service de paiement CinetPay pour le moment.", e);
        }
    }

    // ── Contrat unifié PaymentStrategy (ajouté sans toucher aux méthodes ci-dessus) ─────────────

    @Override
    public PaymentProviderType getProviderType() {
        return PaymentProviderType.CINETPAY;
    }

    @Override
    public PaymentResponseDto initiatePayment(PaymentRequestDto request) {
        InitierPaiementRequestDTO legacyDto = new InitierPaiementRequestDTO(
                request.studentId(),
                OperateurMobileMoney.ORANGE_MONEY, // non utilisé par initierPaiement(...) ci-dessus
                request.amount(),
                request.customerPhoneNumber()
        );
        String paymentUrl = initierPaiement(request.orderId(), legacyDto);
        return PaymentResponseDto.builder()
                .transactionReference(request.orderId())
                .paymentUrl(paymentUrl)
                .status(PaymentStatus.INITIATED)
                .build();
    }

    @Override
    public PaymentResponseDto handleWebhook(WebhookPayloadDto webhookPayload) {
        Map<String, Object> fields = webhookPayload.fields();
        String transId = String.valueOf(fields.get("cpm_trans_id"));
        String resultat = String.valueOf(fields.getOrDefault("cpm_result", ""));
        String transStatus = String.valueOf(fields.getOrDefault("cpm_trans_status", ""));
        String payId = fields.get("cpm_payid") != null ? String.valueOf(fields.get("cpm_payid")) : null;

        boolean accepte = "00".equals(resultat) || "ACCEPTED".equalsIgnoreCase(transStatus);
        return PaymentResponseDto.builder()
                .transactionReference(transId)
                .providerTransactionId(payId)
                .status(accepte ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .rawResponse(fields)
                .build();
    }

    /**
     * Interroge l'API CinetPay "Check Payment Status"
     * (POST /v2/payment/check — https://docs.cinetpay.com/api/1.0-en/checkout/status).
     */
    @Override
    public PaymentResponseDto checkTransactionStatus(String transactionReference) {
        var cinetpay = paiementProperties.getCinetpay();
        Map<String, Object> body = Map.of(
                "apikey", cinetpay.getApiKey(),
                "site_id", cinetpay.getSiteId(),
                "transaction_id", transactionReference
        );

        try {
            String json = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT + "/check"))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode racine = objectMapper.readTree(response.body());
            String statutBrut = racine.path("data").path("status").asText("");

            PaymentStatus statut = switch (statutBrut) {
                case "ACCEPTED" -> PaymentStatus.SUCCESS;
                case "REFUSED" -> PaymentStatus.FAILED;
                case "CANCELLED" -> PaymentStatus.CANCELLED;
                case "" -> PaymentStatus.PENDING;
                default -> PaymentStatus.PENDING;
            };

            return PaymentResponseDto.builder()
                    .transactionReference(transactionReference)
                    .providerTransactionId(racine.path("data").path("payment_method").asText(null))
                    .status(statut)
                    .rawResponse(objectMapper.convertValue(racine, Map.class))
                    .build();

        } catch (java.io.IOException | InterruptedException e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            log.error("Erreur réseau lors de la vérification de statut CinetPay : {}", e.getMessage());
            throw new PaymentProviderException(
                    "Impossible de vérifier le statut du paiement CinetPay pour le moment.", e);
        }
    }

    /**
     * Même formule que {@code WebhookService#verifierSignatureCinetPay} (dupliquée
     * volontairement : cette dernière reste privée et n'est pas modifiée — voir non-régression).
     */
    @Override
    public boolean validateWebhookSignature(WebhookPayloadDto webhookPayload) {
        if (!paiementProperties.getCinetpay().isVerifySignature()) {
            return true;
        }
        Map<String, Object> fields = webhookPayload.fields();
        String apiSecret = paiementProperties.getCinetpay().getApiSecret();
        String siteId = paiementProperties.getCinetpay().getSiteId();
        String input = apiSecret + siteId
                + fields.get("cpm_trans_id") + fields.get("cpm_amount") + fields.get("cpm_currency");
        String attendu = sha256Hex(input);
        Object signature = fields.get("signature");
        return signature != null && attendu.equalsIgnoreCase(String.valueOf(signature));
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new PaymentProviderException("Erreur calcul SHA-256 pour la signature CinetPay", e);
        }
    }
}
