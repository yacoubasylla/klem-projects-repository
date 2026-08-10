package com.klem.coreapi.workflow.application.service;

import com.klem.coreapi.authorization.application.service.AuthorizationService;
import com.klem.coreapi.authorization.domain.model.RoleAssignment;
import com.klem.coreapi.authorization.domain.model.RoleCode;
import com.klem.coreapi.identity.application.service.IdentityService;
import com.klem.coreapi.identity.domain.model.TenantMembership;
import com.klem.coreapi.tenant.application.service.TenantService;
import com.klem.coreapi.tenant.domain.model.Tenant;
import com.klem.coreapi.workflow.domain.event.TenantOnboardedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    @Mock
    private TenantService tenantService;

    @Mock
    private IdentityService identityService;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private ApplicationEventPublisher events;

    private WorkflowService workflowService;

    @BeforeEach
    void setUp() {
        workflowService = new WorkflowService(tenantService, identityService, authorizationService, events);
    }

    @Test
    void onboardTenant_calls_the_three_services_in_order_and_publishes_composite_event() {
        Tenant tenant = Tenant.create("Boutiki pilote", "commerce informel");
        TenantMembership membership = TenantMembership.invite(tenant.getId(), tenant.getId());
        // userId réel généré par TenantMembership.invite(userId, tenantId) — l'ordre des arguments
        // ci-dessus est délibérément permuté pour ne pas dépendre d'un vrai User dans ce test unitaire ;
        // seul membership.getUserId() est utilisé, sa valeur exacte n'a pas d'importance ici.
        RoleAssignment roleAssignment = RoleAssignment.grant(tenant.getId(), membership.getUserId(), RoleCode.ADMIN);

        when(tenantService.createTenant("Boutiki pilote", "commerce informel")).thenReturn(tenant);
        when(identityService.inviteUser(tenant.getId(), "admin@klem.tech", "Admin Boutiki")).thenReturn(membership);
        when(authorizationService.assignRole(tenant.getId(), membership.getUserId(), RoleCode.ADMIN))
                .thenReturn(roleAssignment);

        WorkflowService.TenantOnboardingResult result =
                workflowService.onboardTenant("Boutiki pilote", "commerce informel", "admin@klem.tech", "Admin Boutiki");

        assertThat(result.tenant()).isSameAs(tenant);
        assertThat(result.membership()).isSameAs(membership);
        assertThat(result.roleAssignment()).isSameAs(roleAssignment);
        assertThat(result.adminEmail()).isEqualTo("admin@klem.tech");

        InOrder order = inOrder(tenantService, identityService, authorizationService);
        order.verify(tenantService).createTenant("Boutiki pilote", "commerce informel");
        order.verify(identityService).inviteUser(tenant.getId(), "admin@klem.tech", "Admin Boutiki");
        order.verify(authorizationService).assignRole(tenant.getId(), membership.getUserId(), RoleCode.ADMIN);

        ArgumentCaptor<TenantOnboardedEvent> captor = ArgumentCaptor.forClass(TenantOnboardedEvent.class);
        verify(events).publishEvent(captor.capture());
        assertThat(captor.getValue().tenantId()).isEqualTo(tenant.getId());
        assertThat(captor.getValue().userId()).isEqualTo(membership.getUserId());
    }

    @Test
    void onboardTenant_propagates_failure_from_last_step_without_publishing_event() {
        Tenant tenant = Tenant.create("Fleet-Advance pilote", "logistique");
        TenantMembership membership = TenantMembership.invite(tenant.getId(), tenant.getId());

        when(tenantService.createTenant(any(), any())).thenReturn(tenant);
        when(identityService.inviteUser(any(), any(), any())).thenReturn(membership);
        when(authorizationService.assignRole(eq(tenant.getId()), eq(membership.getUserId()), eq(RoleCode.ADMIN)))
                .thenThrow(new RuntimeException("échec simulé de l'étape 3"));

        // La garantie de rollback réelle vient de @Transactional (comportement Spring déjà établi,
        // exercé par les trois services eux-mêmes) — ce test unitaire vérifie seulement que
        // l'exception se propage bien jusqu'à l'appelant plutôt que d'être avalée, condition
        // nécessaire pour que Spring déclenche ce rollback.
        assertThatThrownBy(() -> workflowService.onboardTenant(
                "Fleet-Advance pilote", "logistique", "admin@klem.tech", "Admin"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("échec simulé de l'étape 3");

        verify(events, never()).publishEvent(any());
    }
}
