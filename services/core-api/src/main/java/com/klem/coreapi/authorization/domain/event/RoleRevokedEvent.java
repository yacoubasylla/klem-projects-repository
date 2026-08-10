package com.klem.coreapi.authorization.domain.event;

import com.klem.coreapi.authorization.domain.model.RoleCode;

import java.time.Instant;
import java.util.UUID;

public record RoleRevokedEvent(UUID tenantId, UUID userId, RoleCode roleCode, Instant occurredAt) {
}
