package com.klem.coreapi.identity.application.service;

import com.klem.coreapi.identity.application.port.TenantMembershipRepository;
import com.klem.coreapi.identity.application.port.UserRepository;
import com.klem.coreapi.identity.domain.event.UserActivatedEvent;
import com.klem.coreapi.identity.domain.event.UserInvitedEvent;
import com.klem.coreapi.identity.domain.exception.UnknownTenantException;
import com.klem.coreapi.identity.domain.exception.UserAlreadyMemberException;
import com.klem.coreapi.identity.domain.model.MembershipStatus;
import com.klem.coreapi.identity.domain.model.TenantMembership;
import com.klem.coreapi.identity.domain.model.User;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentityServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantMembershipRepository membershipRepository;

    @Mock
    private TenantService tenantService;

    @Mock
    private ApplicationEventPublisher events;

    private IdentityService identityService;

    @BeforeEach
    void setUp() {
        identityService = new IdentityService(userRepository, membershipRepository, tenantService, events);
        lenient().when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(membershipRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void getOrProvisionCurrentUser_returns_existing_when_already_linked() {
        User existing = User.provisioned("kc-sub-1", "op@klem.tech", "Opérateur KLEM");
        when(userRepository.findByKeycloakSubject("kc-sub-1")).thenReturn(Optional.of(existing));

        User result = identityService.getOrProvisionCurrentUser("kc-sub-1", "op@klem.tech", "Opérateur KLEM");

        assertThat(result).isSameAs(existing);
        verify(userRepository, never()).save(any());
        verify(events, never()).publishEvent(any());
    }

    @Test
    void getOrProvisionCurrentUser_links_pending_invite_and_activates_memberships() {
        User pending = User.invited("nouveau@klem.tech", "Nouveau membre");
        when(userRepository.findByKeycloakSubject("kc-sub-2")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("nouveau@klem.tech")).thenReturn(Optional.of(pending));

        TenantMembership invitedMembership = TenantMembership.invite(pending.getId(), UUID.randomUUID());
        when(membershipRepository.findByUserId(pending.getId())).thenReturn(List.of(invitedMembership));

        User result = identityService.getOrProvisionCurrentUser("kc-sub-2", "nouveau@klem.tech", "Nouveau membre");

        assertThat(result.isLinked()).isTrue();
        assertThat(result.getKeycloakSubject()).isEqualTo("kc-sub-2");
        assertThat(invitedMembership.getStatus()).isEqualTo(MembershipStatus.ACTIVE);

        ArgumentCaptor<UserActivatedEvent> captor = ArgumentCaptor.forClass(UserActivatedEvent.class);
        verify(events).publishEvent(captor.capture());
        assertThat(captor.getValue().userId()).isEqualTo(pending.getId());
    }

    @Test
    void getOrProvisionCurrentUser_provisions_new_user_when_unknown() {
        when(userRepository.findByKeycloakSubject("kc-sub-3")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("inconnu@klem.tech")).thenReturn(Optional.empty());

        User result = identityService.getOrProvisionCurrentUser("kc-sub-3", "inconnu@klem.tech", "Inconnu");

        assertThat(result.getKeycloakSubject()).isEqualTo("kc-sub-3");
        assertThat(result.getEmail()).isEqualTo("inconnu@klem.tech");
        verify(events, never()).publishEvent(any());
    }

    @Test
    void inviteUser_throws_when_tenant_unknown() {
        UUID tenantId = UUID.randomUUID();
        when(tenantService.tenantExists(tenantId)).thenReturn(false);

        assertThatThrownBy(() -> identityService.inviteUser(tenantId, "x@klem.tech", "X"))
                .isInstanceOf(UnknownTenantException.class);
    }

    @Test
    void inviteUser_creates_new_user_and_membership() {
        UUID tenantId = UUID.randomUUID();
        when(tenantService.tenantExists(tenantId)).thenReturn(true);
        when(userRepository.findByEmail("invite@klem.tech")).thenReturn(Optional.empty());

        TenantMembership membership = identityService.inviteUser(tenantId, "invite@klem.tech", "Invité");

        assertThat(membership.getTenantId()).isEqualTo(tenantId);
        assertThat(membership.getStatus()).isEqualTo(MembershipStatus.INVITED);
        verify(userRepository, times(1)).save(any(User.class));

        ArgumentCaptor<UserInvitedEvent> captor = ArgumentCaptor.forClass(UserInvitedEvent.class);
        verify(events).publishEvent(captor.capture());
        assertThat(captor.getValue().email()).isEqualTo("invite@klem.tech");
    }

    @Test
    void inviteUser_reuses_existing_user_by_email() {
        UUID tenantId = UUID.randomUUID();
        User existing = User.provisioned("kc-sub-4", "deja@klem.tech", "Déjà inscrit");
        when(tenantService.tenantExists(tenantId)).thenReturn(true);
        when(userRepository.findByEmail("deja@klem.tech")).thenReturn(Optional.of(existing));
        when(membershipRepository.findByUserIdAndTenantId(existing.getId(), tenantId)).thenReturn(Optional.empty());

        identityService.inviteUser(tenantId, "deja@klem.tech", "Déjà inscrit");

        verify(userRepository, never()).save(any());
    }

    @Test
    void inviteUser_throws_when_already_member() {
        UUID tenantId = UUID.randomUUID();
        User existing = User.provisioned("kc-sub-5", "membre@klem.tech", "Membre");
        when(tenantService.tenantExists(tenantId)).thenReturn(true);
        when(userRepository.findByEmail("membre@klem.tech")).thenReturn(Optional.of(existing));
        when(membershipRepository.findByUserIdAndTenantId(existing.getId(), tenantId))
                .thenReturn(Optional.of(TenantMembership.invite(existing.getId(), tenantId)));

        assertThatThrownBy(() -> identityService.inviteUser(tenantId, "membre@klem.tech", "Membre"))
                .isInstanceOf(UserAlreadyMemberException.class);
    }
}
