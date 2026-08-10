package com.klem.coreapi.tenant.domain.exception;

import com.klem.coreapi.shared.domain.NotFoundException;

import java.util.UUID;

public class TenantNotFoundException extends NotFoundException {

    public TenantNotFoundException(UUID tenantId) {
        super("Tenant introuvable : " + tenantId);
    }
}
