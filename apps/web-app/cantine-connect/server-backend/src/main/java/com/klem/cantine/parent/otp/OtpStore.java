package com.klem.cantine.parent.otp;

/**
 * Stockage temporaire des codes OTP en attente de vérification (5 minutes, usage unique).
 * Interface séparée de son implémentation pour permettre de substituer un stockage
 * partagé (ex. Redis) à l'implémentation en mémoire par défaut sans toucher {@link
 * com.klem.cantine.parent.otp.service.ParentOtpService} — voir {@link InMemoryOtpStore}.
 */
public interface OtpStore {

    /** Enregistre (ou remplace) le code OTP en attente pour cette clé (numéro de téléphone normalisé). */
    void enregistrer(String cle, String code);

    /**
     * Vérifie le code fourni pour cette clé. Le code est invalidé (retiré du stockage) après
     * un succès (usage unique) ou après un trop grand nombre de tentatives (protection anti
     * brute-force) — dans les deux cas, un nouvel envoi d'OTP est nécessaire.
     */
    boolean verifierEtInvalider(String cle, String code);
}
