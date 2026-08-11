package com.klem.referentielapi.shared.infrastructure.messaging;

import com.klem.referentielapi.shared.domain.event.EntryProposedEvent;
import com.klem.referentielapi.shared.domain.event.EntryStatusChangedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;

/**
 * Pont vers le cluster Kafka partagé du portefeuille — écoute {@link EntryProposedEvent}/
 * {@link EntryStatusChangedEvent}, les deux événements génériques publiés par les quatre domaines
 * du référentiel (voir leur Javadoc pour la justification de cette généricité). Vit dans
 * {@code shared.infrastructure.messaging}, pas dans un domaine particulier : contrairement à
 * {@code core-api} (où {@code PortfolioEventPublisher} vit dans {@code audit}, seul domaine déjà
 * autorisé à dépendre du {@code domain.event} des autres), ici aucune exemption de frontière n'est
 * nécessaire puisque les événements écoutés appartiennent déjà à {@code shared}, pas à un domaine.
 * <p>
 * Topics : {@code {aggregateType}.proposed} / {@code {aggregateType}.status.changed} (ex.
 * {@code texteReglementaire.proposed}), cohérent avec la convention
 * {@code {aggregateType}.{action}} déjà en usage sur core-api.
 */
@Component
public class PortfolioEventPublisher {

    private static final String SOURCE = "referentiel-api-service";
    private static final String SCHEMA_VERSION = "1.0";

    private final KafkaTemplate<String, PortfolioEvent> kafkaTemplate;

    public PortfolioEventPublisher(KafkaTemplate<String, PortfolioEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(EntryProposedEvent event) {
        String eventType = event.aggregateType() + ".proposed";
        publish(eventType, event.eventId().toString(), event.aggregateType(),
                event.aggregateId().toString(), event.occurredAt(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(EntryStatusChangedEvent event) {
        String eventType = event.aggregateType() + ".status.changed";
        publish(eventType, event.eventId().toString(), event.aggregateType(),
                event.aggregateId().toString(), event.occurredAt(), event);
    }

    private void publish(String eventType, String eventId, String aggregateType, String aggregateId,
                          Instant occurredAt, Object payload) {
        PortfolioEvent envelope = new PortfolioEvent(
                eventId,
                eventType,
                SOURCE,
                occurredAt,
                aggregateType,
                aggregateId,
                null,
                payload,
                // tenantId toujours nul : le référentiel est public par nature, pas de notion de
                // tenant (écart de gouvernance documenté, specifications_techniques.md §2.2).
                new PortfolioEvent.Metadata(null, null, null),
                SCHEMA_VERSION
        );
        // Clé = aggregateId : garantit l'ordre des messages relatifs à une même fiche au sein d'une
        // même partition, sans exiger l'ordre global entre fiches différentes.
        kafkaTemplate.send(eventType, aggregateId, envelope);
    }
}
