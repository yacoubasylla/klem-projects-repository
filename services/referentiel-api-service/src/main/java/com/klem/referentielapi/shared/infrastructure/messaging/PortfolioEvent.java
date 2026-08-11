package com.klem.referentielapi.shared.infrastructure.messaging;

import java.time.Instant;

/**
 * Enveloppe d'événement Kafka standard du portefeuille KLEM —
 * {@code shared_architecture/data_pipeline/specifications_techniques.md} §2.1, l'autorité
 * transverse pour la couche bus de données. Même forme (et même écart documenté face à la forme
 * plus plate de {@code KLEM_MASTER_SYSTEM_DIRECTIVE.md} §10) que celle retenue sur {@code core-api}
 * — pas résolu silencieusement ici non plus, juste répliqué pour cohérence inter-services.
 * <p>
 * Simplifications assumées pour cette première intégration, mêmes que sur core-api : {@code actor}
 * toujours nul (aucun contexte d'acteur authentifié encore propagé jusqu'à la couche service au-delà
 * de {@code createdBy}/{@code actorSubject}, qui voyagent dans {@code payload}) ;
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
