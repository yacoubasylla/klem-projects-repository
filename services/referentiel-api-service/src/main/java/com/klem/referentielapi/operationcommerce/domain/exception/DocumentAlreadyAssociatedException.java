package com.klem.referentielapi.operationcommerce.domain.exception;

import com.klem.referentielapi.shared.domain.ConflictException;

import java.util.UUID;

public class DocumentAlreadyAssociatedException extends ConflictException {

    public DocumentAlreadyAssociatedException(UUID operationId, UUID documentId) {
        super("Document " + documentId + " déjà associé à l'opération " + operationId);
    }
}
