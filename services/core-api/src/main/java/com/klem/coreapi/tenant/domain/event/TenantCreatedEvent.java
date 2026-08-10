package com.klem.coreapi.tenant.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Événement de domaine, publié via {@code ApplicationEventPublisher} (Spring, in-process).
 * <p>
 * Ne correspond pas encore à une publication Kafka : la dépendance {@code spring-kafka} n'est pas
 * introduite dans ce Sprint (voir {@code AGENTS.md}, « demander confirmation avant... d'introduire
 * une dépendance à un topic Kafka partagé »). Le pont vers l'événement portefeuille {@code tenant.created}
 * (README.md §5) est un consommateur de cet événement Spring à ajouter quand cette dépendance sera
 * approuvée — ne pas le construire par anticipation.
 */
public record TenantCreatedEvent(UUID tenantId, String name, Instant occurredAt) {
}
