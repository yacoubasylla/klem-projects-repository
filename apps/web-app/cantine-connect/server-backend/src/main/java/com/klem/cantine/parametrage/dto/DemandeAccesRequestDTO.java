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
    @Pattern(regexp = DemandeAccesRequestDTO.REGEX_TELEPHONE_CI,
             message = "Format de téléphone invalide (10 chiffres, ex. 07 08 09 10 11)")
    String telephonePrincipal,

    // Laisser vide si identique au téléphone principal (case "C'est aussi mon WhatsApp")
    @Pattern(regexp = DemandeAccesRequestDTO.REGEX_TELEPHONE_CI,
             message = "Format de téléphone invalide (10 chiffres, ex. 07 08 09 10 11)")
    String telephoneWhatsapp,

    @Pattern(regexp = DemandeAccesRequestDTO.REGEX_TELEPHONE_CI,
             message = "Format de téléphone invalide (10 chiffres, ex. 07 08 09 10 11)")
    String telephoneSecondaire,

    @Email(message = "Format d'email invalide")
    String email,

    @NotBlank(message = "La ville est obligatoire")
    String ville,

    @NotBlank(message = "La commune est obligatoire")
    String commune,

    String quartier
) {
    // Numérotation ivoirienne post-2021 : 10 chiffres commençant par 0 (fixe ou mobile),
    // avec indicatif +225/00225 optionnel et séparateurs (espace, point, tiret) libres entre
    // les chiffres. @Pattern ignore les valeurs null (champs optionnels WhatsApp/secondaire).
    static final String REGEX_TELEPHONE_CI = "^(\\+225|00225)?[\\s.-]*0(?:[\\s.-]*\\d){9}$";
}
