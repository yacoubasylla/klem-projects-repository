package com.klem.cantine.parametrage.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DemandeAccesRequestDTO(

    @NotBlank(message = "Le nom est obligatoire")
    String nom,

    @NotBlank(message = "Le prénom est obligatoire")
    String prenom,

    String fonction,

    @NotBlank(message = "Le numéro de téléphone principal est obligatoire")
    @Pattern(regexp = "^[0-9+\\s\\-]{8,20}$", message = "Format de téléphone invalide")
    String telephonePrincipal,

    // Laisser vide si identique au téléphone principal (case "C'est aussi mon WhatsApp")
    String telephoneWhatsapp,

    String telephoneSecondaire,

    @Email(message = "Format d'email invalide")
    String email,

    @NotBlank(message = "La ville est obligatoire")
    String ville,

    @NotBlank(message = "La commune est obligatoire")
    String commune,

    String quartier
) {}
