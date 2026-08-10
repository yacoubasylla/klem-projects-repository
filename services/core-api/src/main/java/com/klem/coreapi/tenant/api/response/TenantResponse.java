package com.klem.coreapi.tenant.api.response;

import com.klem.coreapi.tenant.domain.model.Tenant;
import com.klem.coreapi.tenant.domain.model.TenantStatus;

import java.time.Instant;
import java.util.UUID;

/** Jamais l'entité {@link Tenant} elle-même n'est renvoyée par le controller — voir DTO obligatoire. */
public record TenantResponse(
        UUID id,
        String name,
        String sector,
        TenantStatus status,
        Instant createdAt
) {

    public static TenantResponse from(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getSector(),
                tenant.getStatus(),
                tenant.getCreatedAt()
        );
    }
}
