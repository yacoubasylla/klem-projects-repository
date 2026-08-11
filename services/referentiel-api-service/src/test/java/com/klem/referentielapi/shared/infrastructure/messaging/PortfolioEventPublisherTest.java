package com.klem.referentielapi.shared.infrastructure.messaging;

import com.klem.referentielapi.shared.domain.StatutPublication;
import com.klem.referentielapi.shared.domain.event.EntryProposedEvent;
import com.klem.referentielapi.shared.domain.event.EntryStatusChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Les méthodes {@code @TransactionalEventListener} sont appelées ici comme de simples méthodes
 * Java — ce test ne couvre pas le câblage {@code AFTER_COMMIT} lui-même (même remarque que sur
 * core-api).
 */
@ExtendWith(MockitoExtension.class)
class PortfolioEventPublisherTest {

    @Mock
    private KafkaTemplate<String, PortfolioEvent> kafkaTemplate;

    private PortfolioEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new PortfolioEventPublisher(kafkaTemplate);
    }

    @Test
    void on_entryProposedEvent_publishes_to_aggregateType_proposed_topic_keyed_by_aggregateId() {
        UUID eventId = UUID.randomUUID();
        UUID aggregateId = UUID.randomUUID();
        publisher.on(new EntryProposedEvent(eventId, "texteReglementaire", aggregateId, "circulaire", "editeur-1", Instant.now()));

        ArgumentCaptor<PortfolioEvent> captor = ArgumentCaptor.forClass(PortfolioEvent.class);
        verify(kafkaTemplate).send(eq("texteReglementaire.proposed"), eq(aggregateId.toString()), captor.capture());

        PortfolioEvent envelope = captor.getValue();
        assertThat(envelope.eventId()).isEqualTo(eventId.toString());
        assertThat(envelope.eventType()).isEqualTo("texteReglementaire.proposed");
        assertThat(envelope.source()).isEqualTo("referentiel-api-service");
        assertThat(envelope.aggregateType()).isEqualTo("texteReglementaire");
        assertThat(envelope.aggregateId()).isEqualTo(aggregateId.toString());
        assertThat(envelope.metadata().tenantId()).isNull();
        assertThat(envelope.schemaVersion()).isEqualTo("1.0");
        assertThat(envelope.payload()).isInstanceOf(EntryProposedEvent.class);
    }

    @Test
    void on_entryStatusChangedEvent_publishes_to_aggregateType_status_changed_topic() {
        UUID aggregateId = UUID.randomUUID();
        publisher.on(new EntryStatusChangedEvent(UUID.randomUUID(), "operationCommerce", aggregateId, "IMP-VEH",
                StatutPublication.EN_REVISION, StatutPublication.PUBLIEE, "admin-1", Instant.now()));

        ArgumentCaptor<PortfolioEvent> captor = ArgumentCaptor.forClass(PortfolioEvent.class);
        verify(kafkaTemplate).send(eq("operationCommerce.status.changed"), eq(aggregateId.toString()), captor.capture());

        assertThat(captor.getValue().aggregateType()).isEqualTo("operationCommerce");
        assertThat(captor.getValue().payload()).isInstanceOf(EntryStatusChangedEvent.class);
    }

    @Test
    void on_entryProposedEvent_for_procedure_uses_procedureMetier_aggregateType_in_topic() {
        UUID aggregateId = UUID.randomUUID();
        publisher.on(new EntryProposedEvent(UUID.randomUUID(), "procedureMetier", aggregateId, "IMP-VEH", "editeur-1", Instant.now()));

        verify(kafkaTemplate).send(eq("procedureMetier.proposed"), eq(aggregateId.toString()), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void on_entryProposedEvent_for_documentRequis_uses_documentRequis_aggregateType_in_topic() {
        UUID aggregateId = UUID.randomUUID();
        publisher.on(new EntryProposedEvent(UUID.randomUUID(), "documentRequis", aggregateId, "CERT-ORIG", "editeur-1", Instant.now()));

        verify(kafkaTemplate).send(eq("documentRequis.proposed"), eq(aggregateId.toString()), org.mockito.ArgumentMatchers.any());
    }
}
