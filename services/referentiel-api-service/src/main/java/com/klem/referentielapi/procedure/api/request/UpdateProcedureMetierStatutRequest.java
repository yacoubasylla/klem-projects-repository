package com.klem.referentielapi.procedure.api.request;

import com.klem.referentielapi.shared.domain.StatutPublication;
import jakarta.validation.constraints.NotNull;

public record UpdateProcedureMetierStatutRequest(
        @NotNull(message = "must not be null")
        StatutPublication statut
) {
}
