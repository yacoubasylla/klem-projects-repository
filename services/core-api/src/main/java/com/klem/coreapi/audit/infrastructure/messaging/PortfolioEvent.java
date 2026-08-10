package com.klem.coreapi.audit.infrastructure.messaging;

import java.time.Instant;

/**
 * Enveloppe d'événement Kafka standard du portefeuille KLEM —
 * {@code shared_architecture/data_pipeline/specifications_techniques.md} §2.1, l'autorité
 * transverse pour la couche bus de données. Existe deux formes documentées dans le dépôt : celle-ci
 * (plus riche : {@code source}, {@code aggregateType}, {@code actor}, {@code metadata} imbriquée)
 * et une forme plus plate dans {@code KLEM_MASTER_SYSTEM_DIRECTIVE.md} §10
 * ({@code eventVersion}/{@code producer}/{@code tenantId} au premier niveau). Celle-ci est retenue
 * ici car explicitement désignée comme référence transverse du cluster Kafka partagé, déjà utilisée
 * par Fleet-Advance/Hinterland-Track — écart documenté plutôt que résolu silencieusement en
 * choisissant l'une des deux sans le signaler.
 * <p>
 * Simplifications assumées pour cette première intégration : {@code actor} toujours nul (aucun
 * contexte d'acteur authentifié encore propagé jusqu'à la couche service, voir
 * {@code TenantController}/{@code UserController} — le JWT s'arrête au controller) ;
 * {@code correlationId}/{@code traceId} non renseignés (pas de propagation de trace distribuée dans
 * ce Sprint).
 */
public record PortfolioEvent(
        String eventId,
        String eventType,
        String source,
        Instant occurredAt,
        String aggregateType,
        String aggregateId,
        Actor actor,
        Object payload,
        Metadata metadata,
        String schemaVersion
) {

    public record Actor(String id, String role) {
    }

    public record Metadata(String correlationId, String traceId, String tenantId) {
    }
}
