package com.klem.referentielapi.operationcommerce.api.request;

import com.klem.referentielapi.shared.domain.StatutPublication;
import jakarta.validation.constraints.NotNull;

public record UpdateOperationCommerceStatutRequest(
        @NotNull(message = "must not be null")
        StatutPublication statut
) {
}
