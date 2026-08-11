package com.klem.referentielapi.operationcommerce.domain.exception;

import com.klem.referentielapi.shared.domain.BadRequestException;

import java.util.UUID;

public class UnknownDocumentRequisException extends BadRequestException {

    public UnknownDocumentRequisException(UUID documentId) {
        super("Document requis inconnu : " + documentId);
    }
}
