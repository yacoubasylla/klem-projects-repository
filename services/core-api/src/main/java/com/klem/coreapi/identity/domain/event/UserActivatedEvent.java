package com.klem.coreapi.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Publié quand un utilisateur {@code INVITED} s'authentifie pour la première fois (lien Keycloak).
 * Voir {@code TenantCreatedEvent} pour la justification d'{@code eventId} généré à la source.
 */
public record UserActivatedEvent(UUID eventId, UUID userId, String keycloakSubject, Instant occurredAt) {
}
