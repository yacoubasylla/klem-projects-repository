package com.klem.cantine.parent.dto;

import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.eleve.entity.Sexe;
import com.klem.cantine.parent.entity.Parent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ParentResponseDTO(
    Long id,
    Long utilisateurId,
    String nom,
    String prenom,
    String email,
    String telephone,
    List<EnfantDTO> enfants,
    LocalDateTime createdAt
) {
    // Champs étendus (etablissementId, classeId, sexe, dateNaissance, ville, commune, quartier)
    // nécessaires pour pré-remplir le formulaire de modification côté parent — évite un second
    // endpoint de lecture réservé au staff (`GET /eleves/{id}`, interdit au rôle PARENT).
    public record EnfantDTO(Long id, String matricule, String nom, String prenom, String statutAcces,
                             Long etablissementId, Long classeId, String classeLibelle,
                             Sexe sexe, LocalDate dateNaissance, String ville, String commune, String quartier,
                             UUID qrCodeToken, BigDecimal solde) {
        public static EnfantDTO from(Eleve e) {
            return new EnfantDTO(e.getId(), e.getMatricule(), e.getNom(), e.getPrenom(), e.getStatutAcces().name(),
                    e.getEtablissement() != null ? e.getEtablissement().getId() : null,
                    e.getClasse() != null ? e.getClasse().getId() : null,
                    e.getClasse() != null ? e.getClasse().getLibelle() : null,
                    e.getSexe(), e.getDateNaissance(), e.getVille(), e.getCommune(), e.getQuartier(),
                    e.getQrCodeToken(), e.getSolde());
        }
    }

    public static ParentResponseDTO from(Parent p) {
        List<EnfantDTO> enfants = p.getEnfants().stream()
                .filter(Eleve::getActif)
                .map(EnfantDTO::from)
                .toList();
        return new ParentResponseDTO(
                p.getId(),
                p.getUtilisateur().getId(),
                p.getUtilisateur().getNom(),
                p.getUtilisateur().getPrenom(),
                p.getUtilisateur().getEmail(),
                p.getUtilisateur().getTelephone(),
                enfants,
                p.getCreatedAt()
        );
    }
}
