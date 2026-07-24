package com.klem.cantine.parametrage.dto;

import jakarta.validation.constraints.NotBlank;

public record ModifierConfigurationRequestDTO(@NotBlank String valeur) {}
