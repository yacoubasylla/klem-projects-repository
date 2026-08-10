package com.klem.coreapi.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

/** Publié quand un utilisateur {@code INVITED} s'authentifie pour la première fois (lien Keycloak). */
public record UserActivatedEvent(UUID userId, String keycloakSubject, Instant occurredAt) {
}
