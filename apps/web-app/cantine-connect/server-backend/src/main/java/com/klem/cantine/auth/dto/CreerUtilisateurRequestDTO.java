package com.klem.cantine.auth.dto;

import com.klem.cantine.auth.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreerUtilisateurRequestDTO(

        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire")
        String prenom,

        @Email(message = "Format email invalide")
        @NotBlank(message = "L'email est obligatoire")
        String email,

        @NotBlank(message = "Le numéro de cellulaire est obligatoire")
        @Pattern(regexp = "^[0-9+ ]{8,20}$", message = "Format de numéro de cellulaire invalide")
        String telephone,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        String motDePasse,

        @NotNull(message = "Le rôle est obligatoire")
        Role role
) {}
