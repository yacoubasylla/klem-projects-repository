package com.klem.coreapi.tenant.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Événement de domaine, publié via {@code ApplicationEventPublisher} (Spring, in-process).
 * <p>
 * {@code eventId} est généré une seule fois, à la source, pour rester stable entre les deux
 * consommateurs cross-domaine : {@code AuditService} (persistance) et
 * {@code PortfolioEventPublisher} (publication Kafka) — sans cette stabilité, l'entrée d'audit et
 * le message Kafka du même fait réel porteraient deux identifiants différents, rendant illusoire
 * toute déduplication côté consommateur Kafka en aval.
 */
public record TenantCreatedEvent(UUID eventId, UUID tenantId, String name, Instant occurredAt) {
}
