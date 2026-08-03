package com.klem.cantine.notification;

import com.klem.cantine.parametrage.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/**
 * Envoi SMS via l'API REST Twilio (https://api.twilio.com). Fournisseur
 * choisi pour démarrer rapidement (compte d'essai gratuit disponible) — un
 * fournisseur local ivoirien pourra être branché plus tard derrière la même
 * interface {@link NotificationSender} sans toucher au reste du code.
 *
 * ⚠️ Sans identifiants Twilio (TWILIO_ACCOUNT_SID/AUTH_TOKEN/FROM_NUMBER en
 * environnement), bascule automatiquement en mode journal (log-only) —
 * comportement sûr par défaut, aucun crash.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SmsNotificationSender implements NotificationSender {

    private final ConfigurationService configurationService;

    @Value("${sms.twilio.account-sid:}")
    private String accountSid;

    @Value("${sms.twilio.auth-token:}")
    private String authToken;

    @Value("${sms.twilio.from-number:}")
    private String fromNumber;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public String getCanal() {
        return "SMS";
    }

    @Override
    public boolean estActif() {
        return "true".equalsIgnoreCase(configurationService.getValeur("NOTIFICATIONS_SMS_ENABLED"));
    }

    @Override
    public void envoyer(String destinataire, String sujet, String corps) {
        if (destinataire == null || destinataire.isBlank()) return;

        if (accountSid.isBlank() || authToken.isBlank() || fromNumber.isBlank()) {
            log.info("[SMS non envoyé — identifiants Twilio absents] à {} — {} : {}", destinataire, sujet, corps);
            return;
        }

        try {
            String vers = auFormatE164(destinataire);
            String formulaire = "To=" + encoder(vers)
                    + "&From=" + encoder(fromNumber)
                    + "&Body=" + encoder(corps);

            String identifiants = Base64.getEncoder()
                    .encodeToString((accountSid + ":" + authToken).getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json"))
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Basic " + identifiants)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formulaire))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("SMS envoyé à {} via Twilio", vers);
            } else {
                log.warn("Échec envoi SMS Twilio à {} — HTTP {} : {}", vers, response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.warn("Échec envoi SMS Twilio à {} : {}", destinataire, e.getMessage());
        }
    }

    /** Numéros locaux ("07XXXXXXXX") convertis en E.164 Côte d'Ivoire (+225...). */
    private String auFormatE164(String numero) {
        String nettoye = numero.replaceAll("[\\s-]", "");
        if (nettoye.startsWith("+")) return nettoye;
        if (nettoye.startsWith("0")) return "+225" + nettoye.substring(1);
        return "+225" + nettoye;
    }

    private String encoder(String valeur) {
        return URLEncoder.encode(valeur, StandardCharsets.UTF_8);
    }
}
