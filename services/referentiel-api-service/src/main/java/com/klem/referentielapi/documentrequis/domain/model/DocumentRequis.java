package com.klem.referentielapi.documentrequis.domain.model;

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
 * Un document requis du référentiel Trade-X (facture, certificat d'origine, connaissement...) —
 * voir {@code specifications_techniques.md} §2.1. Domaine indépendant — rattaché aux opérations
 * commerciales via la jointure {@code operation_document} portée par {@code operationcommerce}.
 */
@Entity
@Table(name = "document_requis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA uniquement — jamais d'instanciation directe hors #propose
public class DocumentRequis {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, length = 50)
    private String code;

    @Column
    private String description;

    @Column(name = "regle_validation")
    private String regleValidation;

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

    private DocumentRequis(UUID id, String nom, String code, String description, String regleValidation,
                            String createdBy, Instant now) {
        this.id = id;
        this.nom = nom;
        this.code = code;
        this.description = description;
        this.regleValidation = regleValidation;
        this.statut = StatutPublication.PROPOSEE;
        this.createdBy = createdBy;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static DocumentRequis propose(String nom, String code, String description, String regleValidation,
                                          String createdBy) {
        return new DocumentRequis(UUID.randomUUID(), nom, code, description, regleValidation, createdBy, Instant.now());
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
