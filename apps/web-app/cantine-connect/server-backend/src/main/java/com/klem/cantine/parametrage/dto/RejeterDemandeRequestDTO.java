package com.klem.cantine.parametrage.dto;

import jakarta.validation.constraints.NotBlank;

public record RejeterDemandeRequestDTO(
    @NotBlank(message = "Le motif de rejet est obligatoire")
    String motif
) {}
