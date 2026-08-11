package com.klem.referentielapi.procedure.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Jointure N:N procédure ↔ texte réglementaire ({@code procedure_texte}, spec §2.1).
 * {@code texteId} référence {@code TexteReglementaire} par UUID simple, jamais par association
 * JPA — l'existence est vérifiée à la création via
 * {@code TexteReglementaireService.exists(UUID)} (lecture peer-à-peer étroite, même motif que
 * {@code core-api}), pas par une contrainte de graphe d'objets cross-domaine.
 */
@Entity
@Table(name = "procedure_texte", uniqueConstraints = @UniqueConstraint(columnNames = {"procedure_id", "texte_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcedureTexte {

    @Id
    private UUID id;

    @Column(name = "procedure_id", nullable = false)
    private UUID procedureId;

    @Column(name = "texte_id", nullable = false)
    private UUID texteId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private ProcedureTexte(UUID id, UUID procedureId, UUID texteId, Instant now) {
        this.id = id;
        this.procedureId = procedureId;
        this.texteId = texteId;
        this.createdAt = now;
    }

    public static ProcedureTexte associate(UUID procedureId, UUID texteId) {
        return new ProcedureTexte(UUID.randomUUID(), procedureId, texteId, Instant.now());
    }
}
