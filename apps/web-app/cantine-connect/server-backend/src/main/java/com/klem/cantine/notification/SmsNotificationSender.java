package com.klem.cantine.notification;

import com.klem.cantine.parametrage.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implémentation stub — aucun fournisseur SMS n'est encore choisi pour la Côte
 * d'Ivoire. Écrit dans les logs au lieu d'envoyer réellement, pour permettre de
 * vérifier le déclenchement des notifications avant de brancher un fournisseur
 * réel (ex. API SMS d'un opérateur local) derrière cette même interface.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SmsNotificationSender implements NotificationSender {

    private final ConfigurationService configurationService;

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
        log.info("[SMS non envoyé — aucun fournisseur configuré] à {} — {} : {}", destinataire, sujet, corps);
    }
}
