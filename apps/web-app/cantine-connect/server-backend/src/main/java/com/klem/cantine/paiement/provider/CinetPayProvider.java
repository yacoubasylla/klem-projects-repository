package com.klem.cantine.paiement.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klem.cantine.paiement.config.PaiementProperties;
import com.klem.cantine.paiement.dto.InitierPaiementRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CinetPayProvider implements PaymentProvider {

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
}
