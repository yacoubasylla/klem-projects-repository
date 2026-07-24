package com.klem.cantine.auth.dto;

import com.klem.cantine.auth.entity.Utilisateur;

public record AuthResponseDTO(
    String token,
    String type,
    Long expiresIn,
    Long id,
    String nom,
    String prenom,
    String email,
    String role
) {
    public static AuthResponseDTO of(String token, long expiresInMs, Utilisateur u) {
        return new AuthResponseDTO(
            token, "Bearer", expiresInMs,
            u.getId(), u.getNom(), u.getPrenom(), u.getEmail(), u.getRole().name()
        );
    }
}
