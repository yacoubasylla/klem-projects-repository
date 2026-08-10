package com.klem.coreapi.authorization.domain.exception;

import com.klem.coreapi.shared.domain.NotFoundException;

import java.util.UUID;

/** Distincte de {@code tenant.domain.exception.TenantNotFoundException} — voir IdentityService §4. */
public class UnknownTenantException extends NotFoundException {

    public UnknownTenantException(UUID tenantId) {
        super("Tenant introuvable : " + tenantId);
    }
}
