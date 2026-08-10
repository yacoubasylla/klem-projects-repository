package com.klem.coreapi.tenant.infrastructure.persistence;

import com.klem.coreapi.tenant.application.port.TenantRepository;
import com.klem.coreapi.tenant.domain.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data implémente {@code save}/{@code findById} de {@link TenantRepository} directement à
 * partir de {@link JpaRepository} — aucune classe d'adaptation supplémentaire nécessaire tant que
 * le port reste ce sous-ensemble minimal.
 */
interface TenantJpaRepository extends JpaRepository<Tenant, UUID>, TenantRepository {
}
