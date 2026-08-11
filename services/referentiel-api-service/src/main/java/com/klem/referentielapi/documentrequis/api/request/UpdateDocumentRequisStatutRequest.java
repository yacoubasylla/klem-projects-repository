package com.klem.referentielapi.documentrequis.api.request;

import com.klem.referentielapi.shared.domain.StatutPublication;
import jakarta.validation.constraints.NotNull;

public record UpdateDocumentRequisStatutRequest(
        @NotNull(message = "must not be null")
        StatutPublication statut
) {
}
