package com.klem.cantine.paiement.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klem.cantine.paiement.config.PaiementProperties;
import com.klem.cantine.paiement.dto.InitierPaiementRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Intégration réelle PayDunya — appelle l'API "Checkout Invoice"
 * (POST /v1/checkout-invoice/create) pour obtenir un token de checkout réel.
 * Doc officielle : https://paydunya.com/developers/checkout
 *
 * ⚠️ Sans clés réelles, l'appel échoue proprement côté PayDunya (identifiants
 * invalides) — traduit en IllegalStateException avec message clair.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PayDunyaProvider implements PaymentProvider {

    private static final String HOTE_TEST = "https://app.paydunya.com/sandbox-api/v1/checkout-invoice/create";
    private static final String HOTE_LIVE = "https://app.paydunya.com/api/v1/checkout-invoice/create";

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
        return "PAYDUNYA";
    }

    @Override
    public String initierPaiement(String referenceInterne, InitierPaiementRequestDTO dto) {
        var paydunya = paiementProperties.getPaydunya();
        String endpoint = "live".equalsIgnoreCase(paydunya.getMode()) ? HOTE_LIVE : HOTE_TEST;

        Map<String, Object> corps = Map.of(
                "invoice", Map.of(
                        "total_amount", dto.montant().intValue(),
                        "description", "Paiement cantine — élève #" + dto.eleveId()
                ),
                "store", Map.of("name", "Cantine Connect"),
                "actions", Map.of(
                        "cancel_url", frontendUrl + "/paiements",
                        "return_url", frontendUrl + "/paiements",
                        "callback_url", backendUrl + "/api/v1/webhooks/paydunya"
                ),
                "custom_data", Map.of("reference_interne", referenceInterne)
        );

        try {
            String json = objectMapper.writeValueAsString(corps);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .header("PAYDUNYA-MASTER-KEY", paydunya.getMasterKey())
                    .header("PAYDUNYA-PRIVATE-KEY", paydunya.getPrivateKey())
                    .header("PAYDUNYA-TOKEN", paydunya.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode racine = objectMapper.readTree(response.body());
            String codeReponse = racine.path("response_code").asText();

            if (!"00".equals(codeReponse)) {
                String texte = racine.path("response_text").asText("Erreur inconnue");
                log.warn("Échec initiation PayDunya (response_code={}) : {}", codeReponse, texte);
                throw new IllegalStateException(
                        "Le service de paiement PayDunya est indisponible ou mal configuré : " + texte);
            }

            String token = racine.path("token").asText(null);
            if (token == null || token.isBlank()) {
                throw new IllegalStateException("Réponse PayDunya invalide : token manquant.");
            }
            return "https://paydunya.com/checkout/invoice/" + token;

        } catch (java.io.IOException | InterruptedException e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            log.error("Erreur réseau lors de l'appel à PayDunya : {}", e.getMessage());
            throw new IllegalStateException(
                    "Impossible de contacter le service de paiement PayDunya pour le moment.", e);
        }
    }
}
