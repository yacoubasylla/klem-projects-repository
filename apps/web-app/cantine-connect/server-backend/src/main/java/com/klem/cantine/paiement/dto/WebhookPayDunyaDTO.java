package com.klem.cantine.paiement.dto;

import java.math.BigDecimal;

public record WebhookPayDunyaDTO(

        // "completed" = succès
        String status,

        // Contient notre referenceInterne
        String token,

        BigDecimal amount,
        String currency,
        String phone,

        // Fourni par PayDunya = sha512(clé privée) — confirme l'origine PayDunya
        String hash
) {
    public boolean estAccepte() {
        return "completed".equalsIgnoreCase(status);
    }
}
