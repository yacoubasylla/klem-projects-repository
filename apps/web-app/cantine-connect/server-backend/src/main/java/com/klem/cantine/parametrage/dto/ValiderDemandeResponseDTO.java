package com.klem.cantine.parametrage.dto;

/**
 * Renvoyé une seule fois à la validation d'une demande — permet à l'admin de
 * communiquer les identifiants au parent même si les notifications
 * email/SMS sont désactivées (NOTIFICATIONS_EMAIL_ENABLED / _SMS_ENABLED).
 */
public record ValiderDemandeResponseDTO(
    DemandeAccesResponseDTO demande,
    String identifiantGenere,
    String motDePasseTemporaire
) {}
