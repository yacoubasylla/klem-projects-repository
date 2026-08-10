package com.klem.coreapi.authorization.infrastructure.messaging;

import com.klem.coreapi.authorization.application.service.AuthorizationService;
import com.klem.coreapi.authorization.domain.event.RoleAssignedEvent;
import com.klem.coreapi.authorization.domain.event.RoleRevokedEvent;
import com.klem.coreapi.authorization.domain.model.RoleAssignment;
import com.klem.coreapi.identity.application.service.IdentityService;
import com.klem.coreapi.identity.domain.event.UserActivatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;
import java.util.UUID;

/**
 * Implémente le point ouvert tranché par l'ADR
 * {@code 2026-08-10-autorisation-core-api-claims-jwt-vs-appel-synchrone.md} : les autres services
 * DataSphere lisent les autorisations via les claims JWT — ce composant est ce qui alimente
 * effectivement ces claims, en poussant chaque attribution/révocation vers le royaume Keycloak.
 * <p>
 * <b>Synchronisation différée</b> : un rôle peut être attribué à un utilisateur {@code INVITED}
 * (jamais authentifié, {@code keycloakSubject} nul) — voir {@code IdentityService#inviteUser} suivi
 * de {@code WorkflowService#onboardTenant}. Dans ce cas, {@link #on(RoleAssignedEvent)} ne peut rien
 * synchroniser (aucun compte Keycloak n'existe encore) et se contente de journaliser l'attente —
 * jamais d'échec silencieux, jamais d'identifiant inventé. Le rattrapage a lieu à
 * {@link #on(UserActivatedEvent)} : dès qu'un utilisateur lie son premier {@code sub} Keycloak,
 * toutes ses attributions déjà enregistrées (potentiellement sur plusieurs tenants) sont poussées
 * d'un coup.
 */
@Component
public class KeycloakRoleSyncPublisher {

    private static final Logger log = LoggerFactory.getLogger(KeycloakRoleSyncPublisher.class);

    private final KeycloakRoleSyncClient keycloakRoleSyncClient;
    private final IdentityService identityService;
    private final AuthorizationService authorizationService;

    public KeycloakRoleSyncPublisher(KeycloakRoleSyncClient keycloakRoleSyncClient,
                                      IdentityService identityService,
                                      AuthorizationService authorizationService) {
        this.keycloakRoleSyncClient = keycloakRoleSyncClient;
        this.identityService = identityService;
        this.authorizationService = authorizationService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleAssignedEvent event) {
        withKeycloakSubject(event.userId(), keycloakUserId ->
                keycloakRoleSyncClient.assignRole(keycloakUserId, event.roleCode()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleRevokedEvent event) {
        withKeycloakSubject(event.userId(), keycloakUserId ->
                keycloakRoleSyncClient.removeRole(keycloakUserId, event.roleCode()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserActivatedEvent event) {
        for (RoleAssignment assignment : authorizationService.getRoleAssignmentsForUser(event.userId())) {
            try {
                keycloakRoleSyncClient.assignRole(event.keycloakSubject(), assignment.getRoleCode());
            } catch (RuntimeException e) {
                // Une attribution qui échoue à se rattraper ne doit pas empêcher les suivantes —
                // chacune sera retentée au prochain événement pertinent (aucun mécanisme de retry
                // dédié dans ce Sprint, voir README.md).
                log.error("Échec du rattrapage de synchronisation Keycloak pour l'utilisateur {} / rôle {}",
                        event.userId(), assignment.getRoleCode(), e);
            }
        }
    }

    private void withKeycloakSubject(UUID userId, java.util.function.Consumer<String> action) {
        Optional<String> keycloakSubject = identityService.getKeycloakSubject(userId);
        if (keycloakSubject.isEmpty()) {
            log.info("Synchronisation Keycloak différée pour l'utilisateur {} — pas encore de compte "
                    + "Keycloak lié, rattrapée à l'activation (UserActivatedEvent).", userId);
            return;
        }
        action.accept(keycloakSubject.get());
    }
}
