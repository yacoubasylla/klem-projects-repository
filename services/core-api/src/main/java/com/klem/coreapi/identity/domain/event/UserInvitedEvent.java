package com.klem.coreapi.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

/** Événement Spring in-process — pas encore ponté vers Kafka, voir README.md §5 et TenantCreatedEvent. */
public record UserInvitedEvent(UUID userId, UUID tenantId, String email, Instant occurredAt) {
}
