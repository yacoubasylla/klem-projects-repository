package com.klem.cantine.eleve.dto;

import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.eleve.entity.PeriodeAbonnement;
import com.klem.cantine.eleve.entity.RegimeAlimentaire;
import com.klem.cantine.eleve.entity.Sexe;
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
    Sexe sexe,
    String photoUrl,
    String ville,
    String commune,
    String quartier,
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
    String certificatMedicalUrl,
    String notesMedicales,
    PeriodeAbonnement periodeAbonnement,
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
            e.getSexe(),
            e.getPhotoUrl(),
            e.getVille(),
            e.getCommune(),
            e.getQuartier(),
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
            e.getCertificatMedicalUrl(),
            e.getNotesMedicales(),
            e.getPeriodeAbonnement(),
            e.getCreatedAt(),
            e.getUpdatedAt()
        );
    }
}
