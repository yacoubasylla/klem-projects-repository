package com.klem.coreapi.identity.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Profil KLEM d'un utilisateur — distinct du {@code sub} Keycloak (voir README.md §1, ligne
 * « Authentification » : core-api ne réimplémente pas l'IdP, il porte le profil applicatif).
 * <p>
 * {@code keycloakSubject} est nul pour un utilisateur invité qui ne s'est jamais encore
 * authentifié ({@link #invited}) ; il est renseigné une fois lié à une session réelle
 * ({@link #linkToKeycloakSubject}) — soit via une invitation honorée, soit via provisionnement
 * « just-in-time » au premier appel de {@code GET /api/v1/users/me} pour un {@code sub} inconnu
 * (voir {@code IdentityService}).
 */
@Entity
@Table(name = "app_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    private UUID id;

    @Column(name = "keycloak_subject", unique = true)
    private String keycloakSubject;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private User(UUID id, String keycloakSubject, String email, String displayName, Instant now) {
        this.id = id;
        this.keycloakSubject = keycloakSubject;
        this.email = email;
        this.displayName = displayName;
        this.createdAt = now;
    }

    /** Créé par une invitation — pas encore authentifié, {@code keycloakSubject} reste nul. */
    public static User invited(String email, String displayName) {
        return new User(UUID.randomUUID(), null, email, displayName, Instant.now());
    }

    /** Provisionné au premier appel authentifié pour un {@code sub} inconnu de core-api. */
    public static User provisioned(String keycloakSubject, String email, String displayName) {
        return new User(UUID.randomUUID(), keycloakSubject, email, displayName, Instant.now());
    }

    public boolean isLinked() {
        return keycloakSubject != null;
    }

    public void linkToKeycloakSubject(String keycloakSubject) {
        this.keycloakSubject = keycloakSubject;
    }
}
