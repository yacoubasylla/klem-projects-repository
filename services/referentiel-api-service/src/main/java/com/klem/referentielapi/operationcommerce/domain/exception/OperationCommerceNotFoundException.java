package com.klem.referentielapi.operationcommerce.domain.exception;

import com.klem.referentielapi.shared.domain.NotFoundException;

import java.util.UUID;

public class OperationCommerceNotFoundException extends NotFoundException {

    public OperationCommerceNotFoundException(UUID id) {
        super("Opération commerciale introuvable : " + id);
    }

    public OperationCommerceNotFoundException(String code) {
        super("Opération commerciale introuvable : " + code);
    }
}
