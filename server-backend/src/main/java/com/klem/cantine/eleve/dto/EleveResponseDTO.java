package com.klem.cantine.eleve.dto;

import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.eleve.entity.RegimeAlimentaire;
import com.klem.cantine.eleve.entity.StatutAcces;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record EleveResponseDTO(
    Long id,
    Long etablissementId,
    String etablissementNom,
    Long classeId,
    String classeLibelle,
    String matricule,
    String nom,
    String prenom,
    LocalDate dateNaissance,
    String photoUrl,
    UUID qrCodeToken,
    StatutAcces statutAcces,
    LocalDate dateFinGrace,
    Boolean estBoursier,
    RegimeAlimentaire regimeAlimentaire,
    BigDecimal solde,
    String parentNom,
    String parentTelephone,
    String parentEmail,
    String allergies,
    String notesMedicales,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static EleveResponseDTO from(Eleve e) {
        return new EleveResponseDTO(
            e.getId(),
            e.getEtablissement().getId(),
            e.getEtablissement().getNom(),
            e.getClasse().getId(),
            e.getClasse().getLibelle(),
            e.getMatricule(),
            e.getNom(),
            e.getPrenom(),
            e.getDateNaissance(),
            e.getPhotoUrl(),
            e.getQrCodeToken(),
            e.getStatutAcces(),
            e.getDateFinGrace(),
            e.getEstBoursier(),
            e.getRegimeAlimentaire(),
            e.getSolde(),
            e.getParentNom(),
            e.getParentTelephone(),
            e.getParentEmail(),
            e.getAllergies(),
            e.getNotesMedicales(),
            e.getCreatedAt(),
            e.getUpdatedAt()
        );
    }
}
