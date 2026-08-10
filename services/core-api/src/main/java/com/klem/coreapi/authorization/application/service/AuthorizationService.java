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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Domaine {@code authorization} — dépend de {@code tenant} et {@code identity} via leurs méthodes
 * étroites respectives (README.md §4 : {@code identity ← authorization}, {@code tenant ← authorization}),
 * jamais de leurs {@code domain.model}/{@code domain.exception} (voir {@code PackageBoundaryRulesTest}).
 * <p>
 * <b>Périmètre de cette tranche</b> : système d'enregistrement des rôles côté {@code core-api}
 * uniquement. La synchronisation vers Keycloak (claim {@code roles} du JWT, voir l'ADR
 * {@code 2026-08-10-autorisation-core-api-claims-jwt-vs-appel-synchrone.md}) n'est **pas**
 * implémentée ici — elle suppose un royaume Keycloak réel et des identifiants d'administration,
 * invérifiables dans ce Sprint. Tant qu'elle n'existe pas, une attribution faite ici n'a aucun
 * effet sur les autorisations réellement appliquées par les autres services DataSphere.
 */
@Service
@Transactional(readOnly = true)
public class AuthorizationService {

    private final RoleAssignmentRepository roleAssignmentRepository;
    private final TenantService tenantService;
    private final IdentityService identityService;
    private final ApplicationEventPublisher events;

    public AuthorizationService(RoleAssignmentRepository roleAssignmentRepository,
                                 TenantService tenantService,
                                 IdentityService identityService,
                                 ApplicationEventPublisher events) {
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.tenantService = tenantService;
        this.identityService = identityService;
        this.events = events;
    }

    public List<RoleAssignment> getRoleAssignments(UUID tenantId) {
        if (!tenantService.tenantExists(tenantId)) {
            throw new UnknownTenantException(tenantId);
        }
        return roleAssignmentRepository.findByTenantId(tenantId);
    }

    @Transactional
    public RoleAssignment assignRole(UUID tenantId, UUID userId, RoleCode roleCode) {
        if (!tenantService.tenantExists(tenantId)) {
            throw new UnknownTenantException(tenantId);
        }
        if (!identityService.isMemberOfTenant(userId, tenantId)) {
            throw new UserNotMemberOfTenantException(userId, tenantId);
        }
        if (roleAssignmentRepository.findByTenantIdAndUserIdAndRoleCode(tenantId, userId, roleCode).isPresent()) {
            throw new RoleAlreadyAssignedException(userId, tenantId, roleCode);
        }

        RoleAssignment assignment = RoleAssignment.grant(tenantId, userId, roleCode);
        roleAssignmentRepository.save(assignment);
        events.publishEvent(new RoleAssignedEvent(tenantId, userId, roleCode, Instant.now()));
        return assignment;
    }

    @Transactional
    public void revokeRole(UUID tenantId, UUID userId, RoleCode roleCode) {
        RoleAssignment assignment = roleAssignmentRepository
                .findByTenantIdAndUserIdAndRoleCode(tenantId, userId, roleCode)
                .orElseThrow(() -> new RoleAssignmentNotFoundException(userId, tenantId, roleCode));

        roleAssignmentRepository.delete(assignment);
        events.publishEvent(new RoleRevokedEvent(tenantId, userId, roleCode, Instant.now()));
    }
}
