package com.klem.cantine.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ModifierUtilisateurRequestDTO(

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

        // Laisser null ou vide pour conserver le mot de passe actuel
        String nouveauMotDePasse
) {}
