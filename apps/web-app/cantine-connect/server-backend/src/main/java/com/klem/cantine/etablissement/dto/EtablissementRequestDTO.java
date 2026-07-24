package com.klem.cantine.etablissement.dto;

import jakarta.validation.constraints.NotBlank;

public record EtablissementRequestDTO(
    @NotBlank(message = "Le nom est obligatoire") String nom,
    String adresse,
    String ville,
    String telephone
) {}
