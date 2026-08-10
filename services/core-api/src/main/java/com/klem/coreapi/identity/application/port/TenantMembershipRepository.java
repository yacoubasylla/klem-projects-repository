package com.klem.coreapi.identity.application.port;

import com.klem.coreapi.identity.domain.model.TenantMembership;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantMembershipRepository {

    TenantMembership save(TenantMembership membership);

    List<TenantMembership> findByUserId(UUID userId);

    Optional<TenantMembership> findByUserIdAndTenantId(UUID userId, UUID tenantId);
}
