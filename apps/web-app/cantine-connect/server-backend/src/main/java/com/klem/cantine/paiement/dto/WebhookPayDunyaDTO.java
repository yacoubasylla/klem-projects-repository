package com.klem.cantine.paiement.dto;

import java.math.BigDecimal;

public record WebhookPayDunyaDTO(

        // "completed" = succès
        String status,

        // Contient notre referenceInterne
        String token,

        BigDecimal amount,
        String currency,
        String phone
) {
    public boolean estAccepte() {
        return "completed".equalsIgnoreCase(status);
    }
}
