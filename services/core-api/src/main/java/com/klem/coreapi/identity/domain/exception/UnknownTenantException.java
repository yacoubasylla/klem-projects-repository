package com.klem.coreapi.identity.domain.exception;

import com.klem.coreapi.shared.domain.NotFoundException;

import java.util.UUID;

/**
 * Levée quand une opération du domaine {@code identity} référence un {@code tenantId} qui
 * n'existe pas — délibérément distincte de {@code tenant.domain.exception.TenantNotFoundException},
 * qui reste interne au domaine {@code tenant} (voir {@code TenantService#tenantExists}).
 */
public class UnknownTenantException extends NotFoundException {

    public UnknownTenantException(UUID tenantId) {
        super("Tenant introuvable : " + tenantId);
    }
}
