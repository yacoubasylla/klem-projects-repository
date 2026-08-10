package com.klem.coreapi.authorization.infrastructure.messaging;

import com.klem.coreapi.authorization.application.service.AuthorizationService;
import com.klem.coreapi.authorization.domain.event.RoleAssignedEvent;
import com.klem.coreapi.authorization.domain.event.RoleRevokedEvent;
import com.klem.coreapi.authorization.domain.model.RoleAssignment;
import com.klem.coreapi.authorization.domain.model.RoleCode;
import com.klem.coreapi.identity.application.service.IdentityService;
import com.klem.coreapi.identity.domain.event.UserActivatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakRoleSyncPublisherTest {

    @Mock
    private KeycloakRoleSyncClient keycloakRoleSyncClient;

    @Mock
    private IdentityService identityService;

    @Mock
    private AuthorizationService authorizationService;

    private KeycloakRoleSyncPublisher publisher;

    private final UUID userId = UUID.randomUUID();
    private final UUID tenantId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        publisher = new KeycloakRoleSyncPublisher(keycloakRoleSyncClient, identityService, authorizationService);
    }

    @Test
    void on_roleAssignedEvent_syncs_immediately_when_user_already_linked() {
        when(identityService.getKeycloakSubject(userId)).thenReturn(Optional.of("kc-sub-1"));

        publisher.on(new RoleAssignedEvent(UUID.randomUUID(), tenantId, userId, RoleCode.CHAUFFEUR, Instant.now()));

        verify(keycloakRoleSyncClient).assignRole("kc-sub-1", RoleCode.CHAUFFEUR);
    }

    @Test
    void on_roleAssignedEvent_defers_when_user_not_yet_linked() {
        when(identityService.getKeycloakSubject(userId)).thenReturn(Optional.empty());

        publisher.on(new RoleAssignedEvent(UUID.randomUUID(), tenantId, userId, RoleCode.CHAUFFEUR, Instant.now()));

        verify(keycloakRoleSyncClient, never()).assignRole(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void on_roleRevokedEvent_syncs_immediately_when_user_already_linked() {
        when(identityService.getKeycloakSubject(userId)).thenReturn(Optional.of("kc-sub-2"));

        publisher.on(new RoleRevokedEvent(UUID.randomUUID(), tenantId, userId, RoleCode.ADMIN, Instant.now()));

        verify(keycloakRoleSyncClient).removeRole("kc-sub-2", RoleCode.ADMIN);
    }

    @Test
    void on_userActivatedEvent_pushes_all_existing_role_assignments() {
        RoleAssignment assignment1 = RoleAssignment.grant(tenantId, userId, RoleCode.OPERATEUR);
        RoleAssignment assignment2 = RoleAssignment.grant(UUID.randomUUID(), userId, RoleCode.CHAUFFEUR);
        when(authorizationService.getRoleAssignmentsForUser(userId)).thenReturn(List.of(assignment1, assignment2));

        publisher.on(new UserActivatedEvent(UUID.randomUUID(), userId, "kc-sub-3", Instant.now()));

        verify(keycloakRoleSyncClient).assignRole("kc-sub-3", RoleCode.OPERATEUR);
        verify(keycloakRoleSyncClient).assignRole("kc-sub-3", RoleCode.CHAUFFEUR);
    }

    @Test
    void on_userActivatedEvent_continues_after_one_assignment_fails() {
        RoleAssignment failing = RoleAssignment.grant(tenantId, userId, RoleCode.OPERATEUR);
        RoleAssignment succeeding = RoleAssignment.grant(UUID.randomUUID(), userId, RoleCode.CHAUFFEUR);
        when(authorizationService.getRoleAssignmentsForUser(userId)).thenReturn(List.of(failing, succeeding));
        doThrow(new RuntimeException("Keycloak indisponible"))
                .when(keycloakRoleSyncClient).assignRole(eq("kc-sub-4"), eq(RoleCode.OPERATEUR));

        publisher.on(new UserActivatedEvent(UUID.randomUUID(), userId, "kc-sub-4", Instant.now()));

        verify(keycloakRoleSyncClient).assignRole("kc-sub-4", RoleCode.CHAUFFEUR);
    }
}
