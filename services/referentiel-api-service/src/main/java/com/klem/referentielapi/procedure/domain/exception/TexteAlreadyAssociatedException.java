package com.klem.referentielapi.procedure.domain.exception;

import com.klem.referentielapi.shared.domain.ConflictException;

import java.util.UUID;

public class TexteAlreadyAssociatedException extends ConflictException {

    public TexteAlreadyAssociatedException(UUID procedureId, UUID texteId) {
        super("Texte " + texteId + " déjà associé à la procédure " + procedureId);
    }
}
