package com.klem.coreapi.authorization.application.service;

import com.klem.coreapi.authorization.application.port.RoleAssignmentRepository;
import com.klem.coreapi.authorization.domain.event.RoleAssignedEvent;
import com.klem.coreapi.authorization.domain.event.RoleRevokedEvent;
import com.klem.coreapi.authorization.domain.exception.RoleAlreadyAssignedException;
import com.klem.coreapi.authorization.domain.exception.RoleAssignmentNotFoundException;
import com.klem.coreapi.authorization.domain.exception.UnknownTenantException;
import com.klem.coreapi.authorization.domain.exception.UserNotMemberOfTenantException;
import com.klem.coreapi.authorization.domain.model.RoleAssignment;
import com.klem.coreapi.authorization.domain.model.RoleCode;
import com.klem.coreapi.identity.application.service.IdentityService;
import com.klem.coreapi.tenant.application.service.TenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private RoleAssignmentRepository roleAssignmentRepository;

    @Mock
    private TenantService tenantService;

    @Mock
    private IdentityService identityService;

    @Mock
    private ApplicationEventPublisher events;

    private AuthorizationService authorizationService;

    private final UUID tenantId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        authorizationService = new AuthorizationService(roleAssignmentRepository, tenantService, identityService, events);
        lenient().when(roleAssignmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void getRoleAssignments_throws_when_tenant_unknown() {
        when(tenantService.tenantExists(tenantId)).thenReturn(false);

        assertThatThrownBy(() -> authorizationService.getRoleAssignments(tenantId))
                .isInstanceOf(UnknownTenantException.class);
    }

    @Test
    void getRoleAssignments_returns_assignments_for_tenant() {
        when(tenantService.tenantExists(tenantId)).thenReturn(true);
        RoleAssignment assignment = RoleAssignment.grant(tenantId, userId, RoleCode.OPERATEUR);
        when(roleAssignmentRepository.findByTenantId(tenantId)).thenReturn(List.of(assignment));

        assertThat(authorizationService.getRoleAssignments(tenantId)).containsExactly(assignment);
    }

    @Test
    void assignRole_throws_when_tenant_unknown() {
        when(tenantService.tenantExists(tenantId)).thenReturn(false);

        assertThatThrownBy(() -> authorizationService.assignRole(tenantId, userId, RoleCode.OPERATEUR))
                .isInstanceOf(UnknownTenantException.class);
    }

    @Test
    void assignRole_throws_when_user_not_member() {
        when(tenantService.tenantExists(tenantId)).thenReturn(true);
        when(identityService.isMemberOfTenant(userId, tenantId)).thenReturn(false);

        assertThatThrownBy(() -> authorizationService.assignRole(tenantId, userId, RoleCode.OPERATEUR))
                .isInstanceOf(UserNotMemberOfTenantException.class);
    }

    @Test
    void assignRole_throws_when_already_assigned() {
        when(tenantService.tenantExists(tenantId)).thenReturn(true);
        when(identityService.isMemberOfTenant(userId, tenantId)).thenReturn(true);
        when(roleAssignmentRepository.findByTenantIdAndUserIdAndRoleCode(tenantId, userId, RoleCode.OPERATEUR))
                .thenReturn(Optional.of(RoleAssignment.grant(tenantId, userId, RoleCode.OPERATEUR)));

        assertThatThrownBy(() -> authorizationService.assignRole(tenantId, userId, RoleCode.OPERATEUR))
                .isInstanceOf(RoleAlreadyAssignedException.class);
    }

    @Test
    void assignRole_creates_assignment_and_publishes_event() {
        when(tenantService.tenantExists(tenantId)).thenReturn(true);
        when(identityService.isMemberOfTenant(userId, tenantId)).thenReturn(true);
        when(roleAssignmentRepository.findByTenantIdAndUserIdAndRoleCode(tenantId, userId, RoleCode.OPERATEUR))
                .thenReturn(Optional.empty());

        RoleAssignment result = authorizationService.assignRole(tenantId, userId, RoleCode.OPERATEUR);

        assertThat(result.getRoleCode()).isEqualTo(RoleCode.OPERATEUR);
        ArgumentCaptor<RoleAssignedEvent> captor = ArgumentCaptor.forClass(RoleAssignedEvent.class);
        verify(events).publishEvent(captor.capture());
        assertThat(captor.getValue().roleCode()).isEqualTo(RoleCode.OPERATEUR);
    }

    @Test
    void revokeRole_throws_when_not_found() {
        when(roleAssignmentRepository.findByTenantIdAndUserIdAndRoleCode(tenantId, userId, RoleCode.OPERATEUR))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorizationService.revokeRole(tenantId, userId, RoleCode.OPERATEUR))
                .isInstanceOf(RoleAssignmentNotFoundException.class);
        verify(roleAssignmentRepository, never()).delete(any());
    }

    @Test
    void revokeRole_deletes_and_publishes_event() {
        RoleAssignment assignment = RoleAssignment.grant(tenantId, userId, RoleCode.OPERATEUR);
        when(roleAssignmentRepository.findByTenantIdAndUserIdAndRoleCode(tenantId, userId, RoleCode.OPERATEUR))
                .thenReturn(Optional.of(assignment));

        authorizationService.revokeRole(tenantId, userId, RoleCode.OPERATEUR);

        verify(roleAssignmentRepository).delete(assignment);
        ArgumentCaptor<RoleRevokedEvent> captor = ArgumentCaptor.forClass(RoleRevokedEvent.class);
        verify(events).publishEvent(captor.capture());
        assertThat(captor.getValue().userId()).isEqualTo(userId);
    }
}
