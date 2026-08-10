package com.klem.coreapi.authorization.domain.event;

import com.klem.coreapi.authorization.domain.model.RoleCode;

import java.time.Instant;
import java.util.UUID;

/** Voir {@code TenantCreatedEvent} pour la justification d'{@code eventId} généré à la source. */
public record RoleAssignedEvent(UUID eventId, UUID tenantId, UUID userId, RoleCode roleCode, Instant occurredAt) {
}
