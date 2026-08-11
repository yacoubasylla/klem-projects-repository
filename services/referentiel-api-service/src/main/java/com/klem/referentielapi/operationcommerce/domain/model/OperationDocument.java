package com.klem.referentielapi.operationcommerce.domain.model;

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
 * Jointure N:N opération ↔ document requis ({@code operation_document}, spec §2.1), portant la
 * condition d'applicabilité (ex. « si valeur CAF > 2M FCFA »). {@code documentId} référence
 * {@code DocumentRequis} par UUID simple, existence vérifiée via
 * {@code DocumentRequisService.exists(UUID)} (lecture peer-à-peer étroite).
 */
@Entity
@Table(name = "operation_document", uniqueConstraints = @UniqueConstraint(columnNames = {"operation_id", "document_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperationDocument {

    @Id
    private UUID id;

    @Column(name = "operation_id", nullable = false)
    private UUID operationId;

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "condition_applicabilite")
    private String conditionApplicabilite;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private OperationDocument(UUID id, UUID operationId, UUID documentId, String conditionApplicabilite, Instant now) {
        this.id = id;
        this.operationId = operationId;
        this.documentId = documentId;
        this.conditionApplicabilite = conditionApplicabilite;
        this.createdAt = now;
    }

    public static OperationDocument associate(UUID operationId, UUID documentId, String conditionApplicabilite) {
        return new OperationDocument(UUID.randomUUID(), operationId, documentId, conditionApplicabilite, Instant.now());
    }
}
