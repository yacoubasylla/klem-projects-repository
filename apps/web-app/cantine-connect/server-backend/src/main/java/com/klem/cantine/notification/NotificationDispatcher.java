package com.klem.cantine.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Point d'entrée unique pour l'envoi de notifications multi-canal. Choisit,
 * parmi les {@link NotificationSender} disponibles (email, SMS...), ceux
 * activés par configuration, et leur transmet la coordonnée adaptée au canal.
 */
@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final List<NotificationSender> senders;

    /**
     * @param emailDestinataire     adresse email du destinataire (ou null)
     * @param telephoneDestinataire numéro de téléphone du destinataire (ou null)
     */
    public void envoyer(String emailDestinataire, String telephoneDestinataire, String sujet, String corps) {
        Map<String, String> destinatairesParCanal = Map.of(
                "EMAIL", emailDestinataire != null ? emailDestinataire : "",
                "SMS", telephoneDestinataire != null ? telephoneDestinataire : ""
        );
        senders.stream()
                .filter(NotificationSender::estActif)
                .forEach(sender -> {
                    String destinataire = destinatairesParCanal.get(sender.getCanal());
                    if (destinataire != null && !destinataire.isBlank()) {
                        sender.envoyer(destinataire, sujet, corps);
                    }
                });
    }
}
