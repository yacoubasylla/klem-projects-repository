package com.klem.cantine.paiement.strategy.enums;

/**
 * État normalisé d'une transaction dans le contrat unifié {@link com.klem.cantine.paiement.strategy.PaymentStrategy}.
 * <p>
 * Se mappe vers l'énumération historique {@link com.klem.cantine.paiement.entity.StatutPaiement}
 * (conservée telle quelle en base de données) via
 * {@code CanteenPaymentServiceImpl#toStatutPaiement(PaymentStatus)} : {@code INITIATED}/{@code PENDING}
 * → {@code EN_ATTENTE}, {@code SUCCESS} → {@code ACCEPTE}, {@code FAILED} → {@code REFUSE},
 * {@code CANCELLED}/{@code EXPIRED} → {@code ANNULE}.
 */
public enum PaymentStatus {
    INITIATED,
    PENDING,
    SUCCESS,
    FAILED,
    CANCELLED,
    EXPIRED
}
