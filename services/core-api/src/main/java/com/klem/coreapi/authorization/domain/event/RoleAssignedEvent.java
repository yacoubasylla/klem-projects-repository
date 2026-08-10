package com.klem.coreapi.authorization.domain.event;

import com.klem.coreapi.authorization.domain.model.RoleCode;

import java.time.Instant;
import java.util.UUID;

/** In-process, pas encore ponté vers Kafka — même remarque que TenantCreatedEvent. */
public record RoleAssignedEvent(UUID tenantId, UUID userId, RoleCode roleCode, Instant occurredAt) {
}
