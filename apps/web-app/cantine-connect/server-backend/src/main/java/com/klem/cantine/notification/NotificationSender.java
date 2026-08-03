package com.klem.cantine.notification;

/**
 * Abstraction multi-canal pour l'envoi de notifications aux parents.
 * Chaque implémentation représente un canal (email, SMS...) activable
 * indépendamment via la configuration (`NOTIFICATIONS_EMAIL_ENABLED`,
 * `NOTIFICATIONS_SMS_ENABLED`). Voir {@link NotificationDispatcher}.
 */
public interface NotificationSender {

    /** Identifiant du canal (ex. "EMAIL", "SMS"). */
    String getCanal();

    /** Le canal est-il activé côté configuration ? */
    boolean estActif();

    /**
     * Envoie la notification. `destinataire` est l'adresse email ou le numéro
     * de téléphone selon le canal — peut être {@code null} (aucune coordonnée
     * disponible), auquel cas l'implémentation ne doit rien faire.
     */
    void envoyer(String destinataire, String sujet, String corps);
}
