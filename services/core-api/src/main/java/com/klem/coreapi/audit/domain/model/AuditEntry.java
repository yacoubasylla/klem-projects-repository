package com.klem.coreapi.audit.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Une entrée du journal d'audit — append-only, jamais modifiée ni supprimée après écriture
 * (`shared_architecture/data_pipeline/specifications_techniques.md` §2.1, table {@code event_log}).
 * <p>
 * {@code eventId} est généré ici, au moment de la capture — les événements de domaine in-process
 * actuels ({@code TenantCreatedEvent}, etc.) n'en portent pas (contrairement à l'enveloppe Kafka du
 * portefeuille, qui elle exige un {@code eventId} porté par le producteur). Pas de déduplication
 * réelle possible tant que cette tranche reste in-process : un même événement Spring n'est livré
 * qu'une fois à chaque listener, la contrainte d'unicité sur {@code eventId} est donc une
 * préparation au pont Kafka futur, pas une protection active aujourd'hui.
 */
@Entity
@Table(name = "audit_entry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditEntry {

    @Id
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "aggregate_id")
    private UUID aggregateId;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    @Lob
    @Column(nullable = false)
    private String payload;

    private AuditEntry(UUID id, UUID eventId, String eventType, UUID tenantId, UUID aggregateId,
                        Instant occurredAt, Instant recordedAt, String payload) {
        this.id = id;
        this.eventId = eventId;
        this.eventType = eventType;
        this.tenantId = tenantId;
        this.aggregateId = aggregateId;
        this.occurredAt = occurredAt;
        this.recordedAt = recordedAt;
        this.payload = payload;
    }

    public static AuditEntry capture(String eventType, UUID tenantId, UUID aggregateId, Instant occurredAt, String payloadJson) {
        return new AuditEntry(UUID.randomUUID(), UUID.randomUUID(), eventType, tenantId, aggregateId,
                occurredAt, Instant.now(), payloadJson);
    }
}
