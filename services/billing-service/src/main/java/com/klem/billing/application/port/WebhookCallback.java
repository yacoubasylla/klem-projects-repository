package com.klem.billing.application.port;

import java.util.Map;

/** Callback brut reçu sur POST /webhooks/{operator}, avant vérification de signature. */
public record WebhookCallback(String rawBody, Map<String, String> headers) {

    public String header(String name) {
        return headers.get(name);
    }
}
