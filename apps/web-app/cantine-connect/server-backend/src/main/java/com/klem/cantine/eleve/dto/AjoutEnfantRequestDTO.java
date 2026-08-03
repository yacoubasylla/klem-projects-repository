package com.klem.cantine.eleve.dto;

import com.klem.cantine.eleve.entity.Sexe;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Ajout d'un enfant par le parent lui-même, depuis son espace authentifié.
 * Les coordonnées du parent (nom, téléphone, email) sont dérivées du compte
 * connecté — jamais saisies ici (évite qu'un parent renseigne les
 * coordonnées d'un tiers).
 */
public record AjoutEnfantRequestDTO(

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

    Sexe sexe,
    LocalDate dateNaissance,

    @NotBlank(message = "La ville est obligatoire")
    String ville,

    @NotBlank(message = "La commune est obligatoire")
    String commune,

    String quartier
) {}
