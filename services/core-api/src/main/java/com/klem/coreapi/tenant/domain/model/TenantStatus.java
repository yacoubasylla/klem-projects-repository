package com.klem.coreapi.tenant.domain.model;

/**
 * Cycle de vie d'un tenant. {@code PENDING} à la création (avant toute activation opérationnelle),
 * {@code ACTIVE} une fois opérationnel, {@code SUSPENDED} en cas de désactivation (impayé, décision
 * administrative, incident) — pas de suppression physique, cohérent avec
 * {@code KLEM_MASTER_SYSTEM_DIRECTIVE.md} §8 (soft delete uniquement si justifié).
 */
public enum TenantStatus {
    PENDING,
    ACTIVE,
    SUSPENDED
}
