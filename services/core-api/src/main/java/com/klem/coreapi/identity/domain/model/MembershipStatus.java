package com.klem.coreapi.identity.domain.model;

/**
 * {@code INVITED} : membre créé par une invitation, pas encore lié à une session Keycloak réelle.
 * {@code ACTIVE} : lié à un {@code sub} Keycloak — l'utilisateur s'est authentifié au moins une
 * fois. Pas de statut de désactivation à ce stade (hors périmètre de cette tranche, voir README.md
 * §5 — pas d'endpoint de désactivation implémenté).
 */
public enum MembershipStatus {
    INVITED,
    ACTIVE
}
