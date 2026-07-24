package com.klem.cantine.auth.dto;

import com.klem.cantine.auth.entity.Role;
import jakarta.validation.constraints.NotNull;

public record ChangerRoleRequestDTO(
        @NotNull(message = "Le rôle est obligatoire")
        Role role
) {}
