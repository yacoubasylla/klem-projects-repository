package com.klem.coreapi.referential.domain.exception;

import com.klem.coreapi.shared.domain.NotFoundException;

public class ReferentialListNotFoundException extends NotFoundException {

    public ReferentialListNotFoundException(String code) {
        super("Référentiel introuvable : " + code);
    }
}
