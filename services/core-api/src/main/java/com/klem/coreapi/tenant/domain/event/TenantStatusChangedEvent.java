package com.klem.coreapi.tenant.domain.event;

import com.klem.coreapi.tenant.domain.model.TenantStatus;

import java.time.Instant;
import java.util.UUID;

/** Même remarque que {@link TenantCreatedEvent} — événement Spring in-process, pas encore Kafka. */
public record TenantStatusChangedEvent(UUID tenantId, TenantStatus previousStatus, TenantStatus newStatus, Instant occurredAt) {
}
