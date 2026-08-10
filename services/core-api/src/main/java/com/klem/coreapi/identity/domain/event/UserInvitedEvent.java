package com.klem.coreapi.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

/** Voir {@code TenantCreatedEvent} pour la justification d'{@code eventId} généré à la source. */
public record UserInvitedEvent(UUID eventId, UUID userId, UUID tenantId, String email, Instant occurredAt) {
}
