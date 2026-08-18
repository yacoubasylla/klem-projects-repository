package com.klem.cantine.parent.otp;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implémentation en mémoire de {@link OtpStore} — expiration 5 minutes, 5 tentatives max.
 * <p>
 * ⚠️ Limite connue : stockage local à l'instance, perdu au redémarrage et non partagé entre
 * plusieurs instances de l'application. Suffisant pour la topologie de déploiement actuelle
 * (une seule instance backend) ; à remplacer par un stockage partagé (Redis) si l'application
 * est un jour déployée en plusieurs instances derrière un répartiteur de charge — {@link
 * OtpStore} est l'interface prévue pour ce remplacement, sans impact sur les appelants.
 */
@Component
public class InMemoryOtpStore implements OtpStore {

    private static final Duration DUREE_VALIDITE = Duration.ofMinutes(5);
    private static final int TENTATIVES_MAX = 5;

    private final Map<String, Entree> entrees = new ConcurrentHashMap<>();

    private record Entree(String code, String email, Instant expiration, AtomicInteger tentatives) {}

    @Override
    public void enregistrer(String cle, String code, String email) {
        entrees.put(cle, new Entree(code, email, Instant.now().plus(DUREE_VALIDITE), new AtomicInteger(0)));
    }

    @Override
    public Optional<String> verifierEtInvalider(String cle, String code) {
        Entree entree = entrees.get(cle);
        if (entree == null || Instant.now().isAfter(entree.expiration())) {
            entrees.remove(cle);
            return Optional.empty();
        }
        if (entree.tentatives().incrementAndGet() > TENTATIVES_MAX) {
            entrees.remove(cle);
            return Optional.empty();
        }
        boolean valide = entree.code().equals(code);
        if (valide) {
            entrees.remove(cle);
            return Optional.ofNullable(entree.email());
        }
        return Optional.empty();
    }
}
