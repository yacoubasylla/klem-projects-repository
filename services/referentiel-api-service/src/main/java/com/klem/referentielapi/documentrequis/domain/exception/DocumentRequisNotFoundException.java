package com.klem.referentielapi.documentrequis.domain.exception;

import com.klem.referentielapi.shared.domain.NotFoundException;

import java.util.UUID;

public class DocumentRequisNotFoundException extends NotFoundException {

    public DocumentRequisNotFoundException(UUID id) {
        super("Document requis introuvable : " + id);
    }
}
