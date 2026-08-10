package com.klem.coreapi.authorization.domain.model;

/**
 * Socle de rôles transverses partagés entre plusieurs projets, tel que défini par
 * {@code shared_architecture/identity_&_iam/specifications_techniques.md} §2 — rôles
 * **applicatifs, scopés à un tenant** (« quel opérateur, dans quelle organisation »).
 * <p>
 * Distinct de {@code PLATFORM_ADMIN}, le rôle plateforme (hors tenant) déjà utilisé pour protéger
 * les endpoints d'administration de {@code core-api} lui-même (`TenantController`,
 * `UserController`) — volontairement **pas** géré par ce domaine : {@code PLATFORM_ADMIN} est
 * accordé hors bande (realm Keycloak), pas via une attribution de rôle par tenant, pour éviter un
 * problème d'amorçage (qui accorderait le premier `PLATFORM_ADMIN` via un endpoint qui exige déjà
 * ce rôle ?).
 */
public enum RoleCode {
    ADMIN,
    OPERATEUR,
    CLIENT,
    MEDECIN,
    PHARMACIEN,
    CHAUFFEUR,
    PRESTATAIRE
}
