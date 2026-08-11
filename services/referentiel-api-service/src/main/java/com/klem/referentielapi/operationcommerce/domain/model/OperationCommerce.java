package com.klem.referentielapi.operationcommerce.domain.model;

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
 * Une opération commerciale du référentiel Trade-X — voir {@code specifications_techniques.md}
 * §2.1. Rattachée à une {@code ProcedureMetier} (référencée par UUID simple {@code procedureId},
 * pas d'association JPA cross-domaine, vérifiée à la création via
 * {@code ProcedureMetierService.exists(UUID)} — lecture peer-à-peer étroite) et à zéro ou plusieurs
 * {@code DocumentRequis} via {@link OperationDocument}.
 */
@Entity
@Table(name = "operation_commerce")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA uniquement — jamais d'instanciation directe hors #propose
public class OperationCommerce {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, length = 50, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeOperation type;

    @Column(name = "procedure_id", nullable = false)
    private UUID procedureId;

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

    private OperationCommerce(UUID id, String nom, String code, TypeOperation type, UUID procedureId,
                               String createdBy, Instant now) {
        this.id = id;
        this.nom = nom;
        this.code = code;
        this.type = type;
        this.procedureId = procedureId;
        this.statut = StatutPublication.PROPOSEE;
        this.createdBy = createdBy;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static OperationCommerce propose(String nom, String code, TypeOperation type, UUID procedureId,
                                             String createdBy) {
        return new OperationCommerce(UUID.randomUUID(), nom, code, type, procedureId, createdBy, Instant.now());
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
