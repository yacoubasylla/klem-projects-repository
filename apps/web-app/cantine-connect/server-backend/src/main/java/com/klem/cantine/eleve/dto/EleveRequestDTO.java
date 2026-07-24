package com.klem.cantine.eleve.dto;

import com.klem.cantine.eleve.entity.RegimeAlimentaire;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record EleveRequestDTO(

    @NotNull(message = "L'établissement est obligatoire")
    Long etablissementId,

    @NotNull(message = "La classe est obligatoire")
    Long classeId,

    @NotBlank(message = "Le matricule est obligatoire")
    String matricule,

    @NotBlank(message = "Le nom est obligatoire")
    String nom,

    @NotBlank(message = "Le prénom est obligatoire")
    String prenom,

    LocalDate dateNaissance,
    String photoUrl,

    // Cantine / Affectation
    Boolean estBoursier,
    RegimeAlimentaire regimeAlimentaire,
    LocalDate dateFinGrace,

    // Contacts / Allergies
    @NotBlank(message = "Le nom du parent est obligatoire")
    String parentNom,

    @NotBlank(message = "Le téléphone du parent est obligatoire")
    @Pattern(regexp = "^[0-9+\\s\\-]{8,20}$", message = "Format de téléphone invalide")
    String parentTelephone,

    String parentEmail,
    String allergies,
    String notesMedicales
) {}
