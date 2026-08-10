package com.klem.coreapi.identity.infrastructure.persistence;

import com.klem.coreapi.identity.application.port.TenantMembershipRepository;
import com.klem.coreapi.identity.domain.model.TenantMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface TenantMembershipJpaRepository extends JpaRepository<TenantMembership, UUID>, TenantMembershipRepository {

    @Override
    List<TenantMembership> findByUserId(UUID userId);

    @Override
    Optional<TenantMembership> findByUserIdAndTenantId(UUID userId, UUID tenantId);
}
