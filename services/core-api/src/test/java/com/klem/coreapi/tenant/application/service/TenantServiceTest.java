package com.klem.coreapi.tenant.application.service;

import com.klem.coreapi.tenant.application.port.TenantRepository;
import com.klem.coreapi.tenant.domain.event.TenantCreatedEvent;
import com.klem.coreapi.tenant.domain.event.TenantStatusChangedEvent;
import com.klem.coreapi.tenant.domain.exception.TenantNotFoundException;
import com.klem.coreapi.tenant.domain.model.Tenant;
import com.klem.coreapi.tenant.domain.model.TenantStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private ApplicationEventPublisher events;

    private TenantService tenantService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        tenantService = new TenantService(tenantRepository, events);
    }

    @Test
    void createTenant_persists_and_publishes_created_event() {
        when(tenantRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Tenant tenant = tenantService.createTenant("KLEM Trade-X pilote", "commerce extérieur");

        assertThat(tenant.getName()).isEqualTo("KLEM Trade-X pilote");
        assertThat(tenant.getStatus()).isEqualTo(TenantStatus.PENDING);

        ArgumentCaptor<TenantCreatedEvent> captor = ArgumentCaptor.forClass(TenantCreatedEvent.class);
        verify(events).publishEvent(captor.capture());
        assertThat(captor.getValue().tenantId()).isEqualTo(tenant.getId());
    }

    @Test
    void getTenant_throws_when_not_found() {
        UUID id = UUID.randomUUID();
        when(tenantRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tenantService.getTenant(id))
                .isInstanceOf(TenantNotFoundException.class);
    }

    @Test
    void changeStatus_updates_and_publishes_status_changed_event() {
        Tenant tenant = Tenant.create("Boutiki pilote", "commerce informel");
        when(tenantRepository.findById(tenant.getId())).thenReturn(Optional.of(tenant));
        when(tenantRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Tenant updated = tenantService.changeStatus(tenant.getId(), TenantStatus.ACTIVE);

        assertThat(updated.getStatus()).isEqualTo(TenantStatus.ACTIVE);

        ArgumentCaptor<TenantStatusChangedEvent> captor = ArgumentCaptor.forClass(TenantStatusChangedEvent.class);
        verify(events).publishEvent(captor.capture());
        assertThat(captor.getValue().previousStatus()).isEqualTo(TenantStatus.PENDING);
        assertThat(captor.getValue().newStatus()).isEqualTo(TenantStatus.ACTIVE);
    }

    @Test
    void changeStatus_does_not_publish_event_when_status_unchanged() {
        Tenant tenant = Tenant.create("Boutiki pilote", "commerce informel");
        when(tenantRepository.findById(tenant.getId())).thenReturn(Optional.of(tenant));
        when(tenantRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        tenantService.changeStatus(tenant.getId(), TenantStatus.PENDING);

        verify(events, never()).publishEvent(any(TenantStatusChangedEvent.class));
    }
}
