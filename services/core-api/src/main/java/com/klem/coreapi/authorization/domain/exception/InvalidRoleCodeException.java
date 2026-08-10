package com.klem.coreapi.authorization.domain.exception;

import com.klem.coreapi.shared.domain.BadRequestException;

public class InvalidRoleCodeException extends BadRequestException {

    public InvalidRoleCodeException(String value) {
        super("Code de rôle invalide : " + value);
    }
}
