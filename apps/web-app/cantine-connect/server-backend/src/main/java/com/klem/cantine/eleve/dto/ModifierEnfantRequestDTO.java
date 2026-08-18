package com.klem.cantine.eleve.dto;

import com.klem.cantine.eleve.entity.Sexe;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Modification d'un enfant par le parent lui-même, depuis son espace authentifié.
 * Mêmes champs que {@link AjoutEnfantRequestDTO} — le matricule n'y figure pas :
 * généré une seule fois à la création, immuable ensuite.
 */
public record ModifierEnfantRequestDTO(

    @NotNull(message = "L'établissement est obligatoire")
    Long etablissementId,

    @NotNull(message = "La classe est obligatoire")
    Long classeId,

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
