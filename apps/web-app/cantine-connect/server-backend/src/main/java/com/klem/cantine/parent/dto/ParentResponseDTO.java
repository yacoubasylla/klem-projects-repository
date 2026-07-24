package com.klem.cantine.parent.dto;

import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.parent.entity.Parent;

import java.time.LocalDateTime;
import java.util.List;

public record ParentResponseDTO(
    Long id,
    Long utilisateurId,
    String nom,
    String prenom,
    String email,
    List<EnfantDTO> enfants,
    LocalDateTime createdAt
) {
    public record EnfantDTO(Long id, String matricule, String nom, String prenom, String statutAcces) {
        public static EnfantDTO from(Eleve e) {
            return new EnfantDTO(e.getId(), e.getMatricule(), e.getNom(), e.getPrenom(), e.getStatutAcces().name());
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
                enfants,
                p.getCreatedAt()
        );
    }
}
