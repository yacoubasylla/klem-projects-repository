package com.klem.referentielapi.procedure.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProcedureMetierRequest(

        @NotBlank(message = "must not be blank")
        @Size(max = 300, message = "must be at most 300 characters")
        String nom,

        @NotBlank(message = "must not be blank")
        @Size(max = 50, message = "must be at most 50 characters")
        String code,

        @Size(max = 2000, message = "must be at most 2000 characters")
        String description,

        @Size(max = 500, message = "must be at most 500 characters")
        String acteurs
) {
}
