package com.klem.cantine.parent.dto;

import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.parent.entity.Parent;

import java.math.BigDecimal;
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
    public record EnfantDTO(Long id, String matricule, String nom, String prenom, String statutAcces,
                             String classeLibelle, UUID qrCodeToken, BigDecimal solde) {
        public static EnfantDTO from(Eleve e) {
            return new EnfantDTO(e.getId(), e.getMatricule(), e.getNom(), e.getPrenom(), e.getStatutAcces().name(),
                    e.getClasse() != null ? e.getClasse().getLibelle() : null,
                    e.getQrCodeToken(), e.getSolde());
        }
    }

    public static ParentResponseDTO from(Parent p) {
        List<EnfantDTO> enfants = p.getEnfants().stream()
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
