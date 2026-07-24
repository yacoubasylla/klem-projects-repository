package com.klem.cantine.auth.dto;

import com.klem.cantine.auth.entity.Role;
import com.klem.cantine.auth.entity.Utilisateur;

import java.time.LocalDateTime;

public record UtilisateurResponseDTO(
        Long id,
        String nom,
        String prenom,
        String email,
        String telephone,
        Role role,
        Boolean actif,
        LocalDateTime createdAt
) {
    public static UtilisateurResponseDTO from(Utilisateur u) {
        return new UtilisateurResponseDTO(
                u.getId(), u.getNom(), u.getPrenom(), u.getEmail(), u.getTelephone(),
                u.getRole(), u.getActif(), u.getCreatedAt()
        );
    }
}
