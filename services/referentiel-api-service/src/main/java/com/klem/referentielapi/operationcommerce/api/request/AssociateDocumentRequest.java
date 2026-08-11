package com.klem.referentielapi.operationcommerce.api.request;

import jakarta.validation.constraints.Size;

public record AssociateDocumentRequest(
        @Size(max = 500, message = "must be at most 500 characters")
        String conditionApplicabilite
) {
}
