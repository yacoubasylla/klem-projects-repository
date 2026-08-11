package com.klem.referentielapi.documentrequis.api.response;

import com.klem.referentielapi.documentrequis.domain.model.DocumentRequis;
import com.klem.referentielapi.shared.domain.StatutPublication;

import java.time.Instant;
import java.util.UUID;

public record DocumentRequisResponse(
        UUID id,
        String nom,
        String code,
        String description,
        String regleValidation,
        StatutPublication statut,
        String createdBy,
        String validatedBy,
        Instant validatedAt,
        Instant createdAt
) {

    public static DocumentRequisResponse from(DocumentRequis document) {
        return new DocumentRequisResponse(
                document.getId(),
                document.getNom(),
                document.getCode(),
                document.getDescription(),
                document.getRegleValidation(),
                document.getStatut(),
                document.getCreatedBy(),
                document.getValidatedBy(),
                document.getValidatedAt(),
                document.getCreatedAt()
        );
    }
}
