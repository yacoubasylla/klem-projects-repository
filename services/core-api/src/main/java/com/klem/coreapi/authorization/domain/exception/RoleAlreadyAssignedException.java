package com.klem.coreapi.authorization.domain.exception;

import com.klem.coreapi.authorization.domain.model.RoleCode;
import com.klem.coreapi.shared.domain.ConflictException;

import java.util.UUID;

public class RoleAlreadyAssignedException extends ConflictException {

    public RoleAlreadyAssignedException(UUID userId, UUID tenantId, RoleCode roleCode) {
        super("Le rôle " + roleCode + " est déjà attribué à l'utilisateur " + userId + " dans le tenant " + tenantId);
    }
}
