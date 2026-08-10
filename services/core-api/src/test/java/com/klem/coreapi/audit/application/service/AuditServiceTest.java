package com.klem.coreapi.audit.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.klem.coreapi.audit.application.port.AuditEntryRepository;
import com.klem.coreapi.audit.domain.model.AuditEntry;
import com.klem.coreapi.authorization.domain.event.RoleAssignedEvent;
import com.klem.coreapi.authorization.domain.model.RoleCode;
import com.klem.coreapi.identity.domain.event.UserActivatedEvent;
import com.klem.coreapi.identity.domain.event.UserInvitedEvent;
import com.klem.coreapi.tenant.domain.event.TenantCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Les méthodes {@code @TransactionalEventListener} sont appelées ici comme de simples méthodes
 * Java — pas via le bus d'événements Spring — pour tester la logique de mapping sans dépendre d'un
 * contexte transactionnel complet. La déclaration {@code AFTER_COMMIT} elle-même (câblage Spring)
 * n'est donc pas vérifiée par ce test unitaire.
 */
@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditEntryRepository auditEntryRepository;

    private AuditService auditService;

    @BeforeEach
    void setUp() {
        // new ObjectMapper() nu ne sait pas sérialiser Instant (contrairement au bean Spring Boot
        // auto-configuré réellement injecté en production, qui embarque JavaTimeModule via
        // JacksonAutoConfiguration) — sans ce module, la capture bascule silencieusement sur le
        // payload de repli et masque le vrai comportement testé. Détecté en écrivant ce test.
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        auditService = new AuditService(auditEntryRepository, objectMapper);
        lenient().when(auditEntryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void on_tenantCreatedEvent_captures_entry_with_tenant_and_payload() {
        UUID eventId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        auditService.on(new TenantCreatedEvent(eventId, tenantId, "Boutiki pilote", Instant.now()));

        ArgumentCaptor<AuditEntry> captor = ArgumentCaptor.forClass(AuditEntry.class);
        verify(auditEntryRepository).save(captor.capture());
        AuditEntry entry = captor.getValue();

        assertThat(entry.getEventId()).isEqualTo(eventId);
        assertThat(entry.getEventType()).isEqualTo("tenant.created");
        assertThat(entry.getTenantId()).isEqualTo(tenantId);
        assertThat(entry.getAggregateId()).isEqualTo(tenantId);
        assertThat(entry.getPayload()).contains("Boutiki pilote");
    }

    @Test
    void on_userActivatedEvent_captures_entry_without_tenant() {
        UUID userId = UUID.randomUUID();
        auditService.on(new UserActivatedEvent(UUID.randomUUID(), userId, "kc-sub-1", Instant.now()));

        ArgumentCaptor<AuditEntry> captor = ArgumentCaptor.forClass(AuditEntry.class);
        verify(auditEntryRepository).save(captor.capture());
        AuditEntry entry = captor.getValue();

        assertThat(entry.getEventType()).isEqualTo("user.activated");
        assertThat(entry.getTenantId()).isNull();
        assertThat(entry.getAggregateId()).isEqualTo(userId);
    }

    @Test
    void on_userInvitedEvent_captures_entry_with_tenant() {
        UUID userId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        auditService.on(new UserInvitedEvent(UUID.randomUUID(), userId, tenantId, "invite@klem.tech", Instant.now()));

        ArgumentCaptor<AuditEntry> captor = ArgumentCaptor.forClass(AuditEntry.class);
        verify(auditEntryRepository).save(captor.capture());
        assertThat(captor.getValue().getEventType()).isEqualTo("user.invited");
        assertThat(captor.getValue().getTenantId()).isEqualTo(tenantId);
    }

    @Test
    void on_roleAssignedEvent_captures_entry_with_role_in_payload() {
        UUID tenantId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        auditService.on(new RoleAssignedEvent(UUID.randomUUID(), tenantId, userId, RoleCode.CHAUFFEUR, Instant.now()));

        ArgumentCaptor<AuditEntry> captor = ArgumentCaptor.forClass(AuditEntry.class);
        verify(auditEntryRepository).save(captor.capture());
        assertThat(captor.getValue().getEventType()).isEqualTo("role.assigned");
        assertThat(captor.getValue().getPayload()).contains("CHAUFFEUR");
    }

    @Test
    void getEntries_delegates_to_repository() {
        UUID tenantId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 20);
        Page<AuditEntry> page = Page.empty();
        when(auditEntryRepository.findByTenantId(tenantId, pageable)).thenReturn(page);

        assertThat(auditService.getEntries(tenantId, pageable)).isSameAs(page);
    }
}
