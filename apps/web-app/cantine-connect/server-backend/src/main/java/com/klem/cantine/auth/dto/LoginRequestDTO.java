package com.klem.cantine.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    @Email @NotBlank(message = "L'email est obligatoire") String email,
    @NotBlank(message = "Le mot de passe est obligatoire") String motDePasse
) {}
