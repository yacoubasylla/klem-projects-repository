package com.klem.coreapi.audit.infrastructure.messaging;

import com.klem.coreapi.authorization.domain.event.RoleAssignedEvent;
import com.klem.coreapi.authorization.domain.model.RoleCode;
import com.klem.coreapi.identity.domain.event.UserActivatedEvent;
import com.klem.coreapi.identity.domain.event.UserInvitedEvent;
import com.klem.coreapi.tenant.domain.event.TenantCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Les méthodes {@code @TransactionalEventListener} sont appelées ici comme de simples méthodes
 * Java, comme pour {@code AuditServiceTest} — même remarque sur ce que ce test unitaire ne couvre
 * pas (câblage {@code AFTER_COMMIT} lui-même).
 */
@ExtendWith(MockitoExtension.class)
class PortfolioEventPublisherTest {

    @Mock
    private KafkaTemplate<String, PortfolioEvent> kafkaTemplate;

    private PortfolioEventPublisher publisher;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        publisher = new PortfolioEventPublisher(kafkaTemplate);
    }

    @Test
    void on_tenantCreatedEvent_publishes_to_tenant_created_topic_keyed_by_tenant_id() {
        UUID eventId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        publisher.on(new TenantCreatedEvent(eventId, tenantId, "Boutiki pilote", Instant.now()));

        ArgumentCaptor<PortfolioEvent> captor = ArgumentCaptor.forClass(PortfolioEvent.class);
        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.eq("tenant.created"),
                org.mockito.ArgumentMatchers.eq(tenantId.toString()), captor.capture());

        PortfolioEvent envelope = captor.getValue();
        assertThat(envelope.eventId()).isEqualTo(eventId.toString());
        assertThat(envelope.eventType()).isEqualTo("tenant.created");
        assertThat(envelope.source()).isEqualTo("core-api");
        assertThat(envelope.aggregateType()).isEqualTo("tenant");
        assertThat(envelope.aggregateId()).isEqualTo(tenantId.toString());
        assertThat(envelope.metadata().tenantId()).isEqualTo(tenantId.toString());
        assertThat(envelope.schemaVersion()).isEqualTo("1.0");
        assertThat(envelope.payload()).isInstanceOf(TenantCreatedEvent.class);
    }

    @Test
    void on_userActivatedEvent_publishes_with_null_tenant_in_metadata() {
        UUID userId = UUID.randomUUID();
        publisher.on(new UserActivatedEvent(UUID.randomUUID(), userId, "kc-sub-1", Instant.now()));

        ArgumentCaptor<PortfolioEvent> captor = ArgumentCaptor.forClass(PortfolioEvent.class);
        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.eq("user.activated"),
                org.mockito.ArgumentMatchers.eq(userId.toString()), captor.capture());

        assertThat(captor.getValue().metadata().tenantId()).isNull();
        assertThat(captor.getValue().aggregateType()).isEqualTo("user");
    }

    @Test
    void on_userInvitedEvent_publishes_to_user_invited_topic() {
        UUID userId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        publisher.on(new UserInvitedEvent(UUID.randomUUID(), userId, tenantId, "invite@klem.tech", Instant.now()));

        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.eq("user.invited"),
                org.mockito.ArgumentMatchers.eq(userId.toString()), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void on_roleAssignedEvent_publishes_with_roleAssignment_aggregate_type() {
        UUID tenantId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        publisher.on(new RoleAssignedEvent(UUID.randomUUID(), tenantId, userId, RoleCode.ADMIN, Instant.now()));

        ArgumentCaptor<PortfolioEvent> captor = ArgumentCaptor.forClass(PortfolioEvent.class);
        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.eq("role.assigned"),
                org.mockito.ArgumentMatchers.eq(userId.toString()), captor.capture());

        assertThat(captor.getValue().aggregateType()).isEqualTo("roleAssignment");
    }
}
