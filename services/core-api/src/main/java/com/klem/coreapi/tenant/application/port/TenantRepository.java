package com.klem.coreapi.tenant.application.port;

import com.klem.coreapi.tenant.domain.model.Tenant;

import java.util.Optional;
import java.util.UUID;

/**
 * Port de persistance du domaine {@code tenant} — l'implémentation vit en
 * {@code infrastructure.persistence}, jamais référencée directement en dehors de ce domaine
 * (voir README.md §4, PackageBoundaryRulesTest).
 */
public interface TenantRepository {

    Tenant save(Tenant tenant);

    Optional<Tenant> findById(UUID id);
}
