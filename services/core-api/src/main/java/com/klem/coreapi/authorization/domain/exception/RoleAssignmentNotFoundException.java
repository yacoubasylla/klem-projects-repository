package com.klem.coreapi.authorization.domain.exception;

import com.klem.coreapi.authorization.domain.model.RoleCode;
import com.klem.coreapi.shared.domain.NotFoundException;

import java.util.UUID;

public class RoleAssignmentNotFoundException extends NotFoundException {

    public RoleAssignmentNotFoundException(UUID userId, UUID tenantId, RoleCode roleCode) {
        super("Aucune attribution du rôle " + roleCode + " pour l'utilisateur " + userId + " dans le tenant " + tenantId);
    }
}
