package com.klem.coreapi.identity.domain.exception;

import com.klem.coreapi.shared.domain.ConflictException;

import java.util.UUID;

public class UserAlreadyMemberException extends ConflictException {

    public UserAlreadyMemberException(String email, UUID tenantId) {
        super("Un membre avec l'e-mail " + email + " existe déjà pour le tenant " + tenantId);
    }
}
