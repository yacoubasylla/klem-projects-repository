package com.klem.referentielapi.operationcommerce.api.response;

import com.klem.referentielapi.operationcommerce.domain.model.OperationCommerce;
import com.klem.referentielapi.operationcommerce.domain.model.TypeOperation;
import com.klem.referentielapi.shared.domain.StatutPublication;

import java.time.Instant;
import java.util.UUID;

public record OperationCommerceResponse(
        UUID id,
        String nom,
        String code,
        TypeOperation type,
        UUID procedureId,
        StatutPublication statut,
        String createdBy,
        String validatedBy,
        Instant validatedAt,
        Instant createdAt
) {

    public static OperationCommerceResponse from(OperationCommerce operation) {
        return new OperationCommerceResponse(
                operation.getId(),
                operation.getNom(),
                operation.getCode(),
                operation.getType(),
                operation.getProcedureId(),
                operation.getStatut(),
                operation.getCreatedBy(),
                operation.getValidatedBy(),
                operation.getValidatedAt(),
                operation.getCreatedAt()
        );
    }
}
