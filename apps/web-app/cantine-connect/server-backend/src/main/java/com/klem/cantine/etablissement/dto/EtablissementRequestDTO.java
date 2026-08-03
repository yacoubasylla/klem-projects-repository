package com.klem.cantine.etablissement.dto;

import jakarta.validation.constraints.NotBlank;

public record EtablissementRequestDTO(
    @NotBlank(message = "Le nom est obligatoire") String nom,
    String adresse,
    String ville,
    String telephone,
    // Surcharge du délai de grâce global (jours) — laisser vide pour utiliser DELAI_GRACE_JOURS_DEFAUT
    Integer delaiGraceJours
) {}
