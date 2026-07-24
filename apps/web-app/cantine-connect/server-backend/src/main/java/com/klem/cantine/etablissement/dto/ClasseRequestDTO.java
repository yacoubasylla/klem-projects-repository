package com.klem.cantine.etablissement.dto;

import jakarta.validation.constraints.NotBlank;

public record ClasseRequestDTO(
    @NotBlank(message = "Le libellé de la classe est obligatoire") String libelle,
    String anneeScolaire
) {}
