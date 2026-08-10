package com.klem.coreapi.audit.api.response;

import com.klem.coreapi.audit.domain.model.AuditEntry;

import java.time.Instant;
import java.util.UUID;

public record AuditEntryResponse(
        UUID id,
        String eventType,
        UUID tenantId,
        UUID aggregateId,
        Instant occurredAt,
        Instant recordedAt,
        String payload
) {

    public static AuditEntryResponse from(AuditEntry entry) {
        return new AuditEntryResponse(
                entry.getId(),
                entry.getEventType(),
                entry.getTenantId(),
                entry.getAggregateId(),
                entry.getOccurredAt(),
                entry.getRecordedAt(),
                entry.getPayload()
        );
    }
}
