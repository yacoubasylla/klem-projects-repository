package com.klem.cantine.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;

/**
 * Envoi WhatsApp via l'API Business de Twilio (mêmes identifiants de compte que {@link
 * SmsNotificationSender} — Twilio expose WhatsApp sur la même API REST Messages, avec un
 * préfixe {@code whatsapp:} sur les numéros From/To et un numéro expéditeur WhatsApp dédié).
 * <p>
 * ⚠️ Sans identifiants Twilio ou sans numéro expéditeur WhatsApp
 * (TWILIO_WHATSAPP_FROM_NUMBER en environnement), bascule automatiquement en mode journal
 * (log-only) — même comportement sûr par défaut que {@link SmsNotificationSender}.
 * <p>
 * ⚠️ Depuis les évolutions de l'API WhatsApp Business, un message envoyé à l'initiative du
 * business (comme un OTP) est refusé par Twilio en texte libre ({@code Body}) — erreur 21654
 * "ContentSid Required" — même en Sandbox après le "join". Un Content Template pré-créé
 * (variable unique {@code {{1}}}) est nécessaire : voir {@code TWILIO_WHATSAPP_CONTENT_SID}.
 * Sans elle, {@code Body} est utilisé tel quel (fonctionne sur un numéro WhatsApp Business
 * réel en dehors de ce cas Sandbox/nouveau compte, ou si Twilio assouplit cette exigence).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WhatsAppNotificationSender implements NotificationSender {

    private final ConfigurationService configurationService;
    private final ObjectMapper objectMapper;

    @Value("${sms.twilio.account-sid:}")
    private String accountSid;

    @Value("${sms.twilio.auth-token:}")
    private String authToken;

    @Value("${sms.twilio.whatsapp-from-number:}")
    private String fromNumber;

    /** SID (préfixe "HX...") du Content Template Twilio à variable unique {{1}} — voir JavaDoc de la classe. */
    @Value("${sms.twilio.whatsapp-content-sid:}")
    private String contentSid;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public String getCanal() {
        return "WHATSAPP";
    }

    @Override
    public boolean estActif() {
        return "true".equalsIgnoreCase(configurationService.getValeur("NOTIFICATIONS_WHATSAPP_ENABLED"));
    }

    @Override
    public void envoyer(String destinataire, String sujet, String corps) {
        if (destinataire == null || destinataire.isBlank()) return;

        if (accountSid.isBlank() || authToken.isBlank() || fromNumber.isBlank()) {
            log.info("[WhatsApp non envoyé — identifiants Twilio absents] à {} — {} : {}", destinataire, sujet, corps);
            return;
        }

        try {
            String vers = "whatsapp:" + auFormatE164(destinataire);
            String formulaire = "To=" + encoder(vers) + "&From=" + encoder("whatsapp:" + fromNumber);
            formulaire += contentSid.isBlank()
                    ? "&Body=" + encoder(corps)
                    : "&ContentSid=" + encoder(contentSid) + "&ContentVariables=" + encoder(variablesJson(corps));

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
                log.info("WhatsApp envoyé à {} via Twilio", vers);
            } else {
                log.warn("Échec envoi WhatsApp Twilio à {} — HTTP {} : {}", vers, response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.warn("Échec envoi WhatsApp Twilio à {} : {}", destinataire, e.getMessage());
        }
    }

    /**
     * Numéros locaux ("0554025100", 10 chiffres depuis la réforme de numérotation 2021)
     * convertis en E.164 Côte d'Ivoire (+225...) — le "0" initial fait partie du numéro
     * d'abonné depuis cette réforme, il n'est pas un préfixe de tri à retirer (contrairement à
     * la plupart des autres pays) : "0554025100" → "+2250554025100", pas "+225554025100".
     */
    private String auFormatE164(String numero) {
        String nettoye = numero.replaceAll("[\\s-]", "");
        if (nettoye.startsWith("+")) return nettoye;
        return "+225" + nettoye;
    }

    private String encoder(String valeur) {
        return URLEncoder.encode(valeur, StandardCharsets.UTF_8);
    }

    /** {@code {"1": corps}} — sérialisé proprement (échappement des retours à la ligne, guillemets...). */
    private String variablesJson(String corps) {
        try {
            return objectMapper.writeValueAsString(Map.of("1", corps));
        } catch (Exception e) {
            throw new IllegalStateException("Erreur sérialisation ContentVariables WhatsApp", e);
        }
    }
}
