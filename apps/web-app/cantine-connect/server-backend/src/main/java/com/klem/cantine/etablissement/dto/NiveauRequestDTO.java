package com.klem.cantine.etablissement.dto;

import jakarta.validation.constraints.NotBlank;

public record NiveauRequestDTO(
    @NotBlank(message = "Le libellé du niveau est obligatoire") String libelle,
    Integer ordre
) {}
