package com.klem.cantine.parent.otp;

import java.util.Optional;

/**
 * Stockage temporaire des codes OTP en attente de vérification (5 minutes, usage unique).
 * Interface séparée de son implémentation pour permettre de substituer un stockage
 * partagé (ex. Redis) à l'implémentation en mémoire par défaut sans toucher {@link
 * com.klem.cantine.parent.otp.service.ParentOtpService} — voir {@link InMemoryOtpStore}.
 */
public interface OtpStore {

    /**
     * Enregistre (ou remplace) le code OTP en attente pour cette clé (numéro de téléphone
     * normalisé). {@code email} est conservé le temps de la vérification pour permettre la
     * création d'un compte parent si ce numéro n'en a pas encore (voir {@link
     * com.klem.cantine.parent.otp.service.ParentOtpService#verifierOtp}).
     */
    void enregistrer(String cle, String code, String email);

    /**
     * Vérifie le code fourni pour cette clé et retourne l'email associé si valide. Le code est
     * invalidé (retiré du stockage) après un succès (usage unique) ou après un trop grand nombre
     * de tentatives (protection anti brute-force) — dans les deux cas, un nouvel envoi d'OTP est
     * nécessaire.
     * @return l'email fourni à l'envoi si le code est correct et non expiré, vide sinon
     */
    Optional<String> verifierEtInvalider(String cle, String code);
}
