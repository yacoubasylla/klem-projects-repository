package com.klem.referentielapi.procedure.domain.exception;

import com.klem.referentielapi.shared.domain.NotFoundException;

import java.util.UUID;

public class ProcedureMetierNotFoundException extends NotFoundException {

    public ProcedureMetierNotFoundException(UUID id) {
        super("Procédure métier introuvable : " + id);
    }
}
