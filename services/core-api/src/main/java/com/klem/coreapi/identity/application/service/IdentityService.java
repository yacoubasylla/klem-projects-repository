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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Cas d'usage du domaine {@code identity}. Dépend de {@code tenant} via {@link TenantService}
 * uniquement (jamais directement de {@code tenant.domain}, voir sa Javadoc et
 * {@code PackageBoundaryRulesTest}) — dépendance autorisée par README.md §4.
 */
@Service
@Transactional(readOnly = true)
public class IdentityService {

    private final UserRepository userRepository;
    private final TenantMembershipRepository membershipRepository;
    private final TenantService tenantService;
    private final ApplicationEventPublisher events;

    public IdentityService(UserRepository userRepository,
                            TenantMembershipRepository membershipRepository,
                            TenantService tenantService,
                            ApplicationEventPublisher events) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.tenantService = tenantService;
        this.events = events;
    }

    /**
     * Résout le profil de l'appelant authentifié à partir du {@code sub} Keycloak. Trois cas :
     * (1) déjà lié — retour direct ; (2) invitation en attente pour cet e-mail — liaison + activation
     * des adhésions {@code INVITED} ; (3) inconnu — provisionnement « just-in-time ».
     * <p>
     * Lacune connue, non traitée par cette tranche : si l'e-mail du JWT diffère de celui déjà
     * enregistré pour un utilisateur déjà lié à un autre {@code sub}, le cas (2) ne matche pas
     * (garde {@code !isLinked()}) et retombe sur (3) — provisionnement d'un second enregistrement.
     * La contrainte d'unicité sur {@code email} ferait alors échouer la sauvegarde plutôt que de
     * silencieusement dupliquer ; ce scénario suppose un changement d'e-mail côté IdP non
     * synchronisé, hors périmètre de ce Sprint.
     */
    @Transactional
    public User getOrProvisionCurrentUser(String keycloakSubject, String email, String displayName) {
        Optional<User> linked = userRepository.findByKeycloakSubject(keycloakSubject);
        if (linked.isPresent()) {
            return linked.get();
        }

        Optional<User> pendingInvite = userRepository.findByEmail(email).filter(u -> !u.isLinked());
        if (pendingInvite.isPresent()) {
            User user = pendingInvite.get();
            user.linkToKeycloakSubject(keycloakSubject);
            userRepository.save(user);
            activatePendingMemberships(user.getId());
            events.publishEvent(new UserActivatedEvent(UUID.randomUUID(), user.getId(), keycloakSubject, Instant.now()));
            return user;
        }

        User provisioned = User.provisioned(keycloakSubject, email, displayName);
        return userRepository.save(provisioned);
    }

    public List<TenantMembership> getMemberships(UUID userId) {
        return membershipRepository.findByUserId(userId);
    }

    /**
     * Vérification étroite exposée aux autres domaines (ex. {@code authorization} avant
     * d'attribuer un rôle) — même motif que {@code TenantService#tenantExists} : ne fuit ni
     * {@link User} ni {@link TenantMembership}, internes à {@code identity}
     * (voir {@code PackageBoundaryRulesTest}).
     */
    public boolean isMemberOfTenant(UUID userId, UUID tenantId) {
        return membershipRepository.findByUserIdAndTenantId(userId, tenantId).isPresent();
    }

    @Transactional
    public TenantMembership inviteUser(UUID tenantId, String email, String displayName) {
        if (!tenantService.tenantExists(tenantId)) {
            throw new UnknownTenantException(tenantId);
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.invited(email, displayName)));

        if (membershipRepository.findByUserIdAndTenantId(user.getId(), tenantId).isPresent()) {
            throw new UserAlreadyMemberException(email, tenantId);
        }

        TenantMembership membership = TenantMembership.invite(user.getId(), tenantId);
        membershipRepository.save(membership);
        events.publishEvent(new UserInvitedEvent(UUID.randomUUID(), user.getId(), tenantId, email, Instant.now()));
        return membership;
    }

    private void activatePendingMemberships(UUID userId) {
        for (TenantMembership membership : membershipRepository.findByUserId(userId)) {
            if (membership.getStatus() == MembershipStatus.INVITED) {
                membership.activate();
                membershipRepository.save(membership);
            }
        }
    }
}
