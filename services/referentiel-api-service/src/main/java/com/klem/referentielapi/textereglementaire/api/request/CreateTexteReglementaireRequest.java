package com.klem.referentielapi.textereglementaire.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateTexteReglementaireRequest(

        @NotBlank(message = "must not be blank")
        @Size(max = 500, message = "must be at most 500 characters")
        String titre,

        @NotBlank(message = "must not be blank")
        @Size(max = 50, message = "must be at most 50 characters")
        String type,

        LocalDate datePublication,

        @Size(max = 200, message = "must be at most 200 characters")
        String reference,

        @Size(max = 200, message = "must be at most 200 characters")
        String domaine,

        @Size(max = 500, message = "must be at most 500 characters")
        String urlSource
) {
}
