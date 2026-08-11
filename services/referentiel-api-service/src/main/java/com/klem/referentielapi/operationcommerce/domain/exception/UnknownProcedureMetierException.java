package com.klem.referentielapi.operationcommerce.domain.exception;

import com.klem.referentielapi.shared.domain.BadRequestException;

import java.util.UUID;

public class UnknownProcedureMetierException extends BadRequestException {

    public UnknownProcedureMetierException(UUID procedureId) {
        super("Procédure métier inconnue : " + procedureId);
    }
}
