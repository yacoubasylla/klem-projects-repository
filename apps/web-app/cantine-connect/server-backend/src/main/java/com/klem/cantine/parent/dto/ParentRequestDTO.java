package com.klem.cantine.parent.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ParentRequestDTO(
    @NotNull(message = "L'identifiant de l'utilisateur est obligatoire")
    Long utilisateurId,

    List<Long> eleveIds
) {}
