package com.klem.referentielapi.procedure.api.response;

import com.klem.referentielapi.procedure.domain.model.ProcedureMetier;
import com.klem.referentielapi.shared.domain.StatutPublication;

import java.time.Instant;
import java.util.UUID;

public record ProcedureMetierResponse(
        UUID id,
        String nom,
        String code,
        String description,
        String acteurs,
        StatutPublication statut,
        String createdBy,
        String validatedBy,
        Instant validatedAt,
        Instant createdAt
) {

    public static ProcedureMetierResponse from(ProcedureMetier procedure) {
        return new ProcedureMetierResponse(
                procedure.getId(),
                procedure.getNom(),
                procedure.getCode(),
                procedure.getDescription(),
                procedure.getActeurs(),
                procedure.getStatut(),
                procedure.getCreatedBy(),
                procedure.getValidatedBy(),
                procedure.getValidatedAt(),
                procedure.getCreatedAt()
        );
    }
}
