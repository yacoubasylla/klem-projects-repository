package com.klem.referentielapi.textereglementaire.api.request;

import com.klem.referentielapi.shared.domain.StatutPublication;
import jakarta.validation.constraints.NotNull;

public record UpdateTexteReglementaireStatutRequest(
        @NotNull(message = "must not be null")
        StatutPublication statut
) {
}
