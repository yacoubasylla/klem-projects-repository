package com.klem.referentielapi.procedure.domain.model;

import com.klem.referentielapi.shared.domain.InvalidStatutTransitionException;
import com.klem.referentielapi.shared.domain.StatutPublication;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Une procédure métier du référentiel Trade-X (import/export/transit/change...) — voir
 * {@code specifications_techniques.md} §2.1. Rattachée à zéro ou plusieurs
 * {@code TexteReglementaire} via la table de jointure {@link ProcedureTexte}, référencée par UUID
 * simple (pas d'association JPA cross-domaine, cohérent avec le patron core-api).
 */
@Entity
@Table(name = "procedure_metier")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA uniquement — jamais d'instanciation directe hors #propose
public class ProcedureMetier {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, length = 50)
    private String code;

    @Column
    private String description;

    @Column
    private String acteurs;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutPublication statut;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "validated_by")
    private String validatedBy;

    @Column(name = "validated_at")
    private Instant validatedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private ProcedureMetier(UUID id, String nom, String code, String description, String acteurs,
                             String createdBy, Instant now) {
        this.id = id;
        this.nom = nom;
        this.code = code;
        this.description = description;
        this.acteurs = acteurs;
        this.statut = StatutPublication.PROPOSEE;
        this.createdBy = createdBy;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static ProcedureMetier propose(String nom, String code, String description, String acteurs,
                                           String createdBy) {
        return new ProcedureMetier(UUID.randomUUID(), nom, code, description, acteurs, createdBy, Instant.now());
    }

    public void changeStatus(StatutPublication newStatus, String actorSubject) {
        if (!this.statut.canTransitionTo(newStatus)) {
            throw new InvalidStatutTransitionException(this.statut, newStatus);
        }
        this.statut = newStatus;
        this.updatedAt = Instant.now();
        if (newStatus == StatutPublication.PUBLIEE || newStatus == StatutPublication.REJETEE) {
            this.validatedBy = actorSubject;
            this.validatedAt = Instant.now();
        }
    }
}
