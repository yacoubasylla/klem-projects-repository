package com.klem.coreapi.workflow.application.service;

import com.klem.coreapi.authorization.application.service.AuthorizationService;
import com.klem.coreapi.authorization.domain.model.RoleAssignment;
import com.klem.coreapi.authorization.domain.model.RoleCode;
import com.klem.coreapi.identity.application.service.IdentityService;
import com.klem.coreapi.identity.domain.model.TenantMembership;
import com.klem.coreapi.tenant.application.service.TenantService;
import com.klem.coreapi.tenant.domain.model.Tenant;
import com.klem.coreapi.workflow.domain.event.TenantOnboardedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Domaine {@code workflow} — orchestrateur privilégié : dépend librement de {@code tenant},
 * {@code identity}, {@code authorization} (README.md §4), y compris de leurs entités
 * {@code domain.model} (exemption ajoutée à {@code PackageBoundaryRulesTest} règle 7
 * spécifiquement pour ce domaine). Rien ne dépend de {@code workflow} (règle 6).
 * <p>
 * Pas de {@code application.port} ni de {@code domain.model} propres : ce Sprint ne persiste
 * aucun état d'exécution de workflow (pas de table {@code workflow_execution}) — l'atomicité vient
 * entièrement de {@code @Transactional} ci-dessous, qui fait de l'enchaînement des trois appels une
 * seule transaction physique. Suffisant pour un enchaînement synchrone et court ; un futur workflow
 * long/asynchrone (au sens {@code knowledge/04-backend-spring-boot.md}, jobs) justifierait un
 * modèle de persistance dédié, pas celui-ci.
 */
@Service
public class WorkflowService {

    private final TenantService tenantService;
    private final IdentityService identityService;
    private final AuthorizationService authorizationService;
    private final ApplicationEventPublisher events;

    public WorkflowService(TenantService tenantService,
                            IdentityService identityService,
                            AuthorizationService authorizationService,
                            ApplicationEventPublisher events) {
        this.tenantService = tenantService;
        this.identityService = identityService;
        this.authorizationService = authorizationService;
        this.events = events;
    }

    /**
     * Crée un tenant, y invite son premier utilisateur, et lui attribue le rôle {@code ADMIN} —
     * en une seule transaction : si l'une des trois étapes échoue, aucune des trois n'est
     * persistée (contrairement à trois appels API séparés côté client, où un échec à l'étape 3
     * laisserait un tenant et une invitation orphelins).
     */
    @Transactional
    public TenantOnboardingResult onboardTenant(String tenantName, String tenantSector,
                                                 String adminEmail, String adminDisplayName) {
        Tenant tenant = tenantService.createTenant(tenantName, tenantSector);
        TenantMembership membership = identityService.inviteUser(tenant.getId(), adminEmail, adminDisplayName);
        RoleAssignment roleAssignment = authorizationService.assignRole(tenant.getId(), membership.getUserId(), RoleCode.ADMIN);

        events.publishEvent(new TenantOnboardedEvent(tenant.getId(), membership.getUserId(), Instant.now()));

        return new TenantOnboardingResult(tenant, membership, roleAssignment, adminEmail);
    }

    /**
     * Porteur de résultat, pas une entité — jamais persisté, jamais renvoyé tel quel par l'API.
     * {@code adminEmail} est repris directement du paramètre d'entrée plutôt que relu depuis
     * {@code TenantMembership} (qui ne porte pas l'e-mail — seul {@code User} le fait, et
     * {@code IdentityService.inviteUser} ne renvoie que l'adhésion) : plus simple que de changer
     * la signature d'une méthode déjà utilisée par {@code UserController}.
     */
    public record TenantOnboardingResult(Tenant tenant, TenantMembership membership,
                                          RoleAssignment roleAssignment, String adminEmail) {
    }
}
