package com.klem.coreapi.authorization.api.controller;

import com.klem.coreapi.authorization.api.response.RoleAssignmentResponse;
import com.klem.coreapi.authorization.application.service.AuthorizationService;
import com.klem.coreapi.authorization.domain.exception.InvalidRoleCodeException;
import com.klem.coreapi.authorization.domain.model.RoleAssignment;
import com.klem.coreapi.authorization.domain.model.RoleCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Réservé à {@code PLATFORM_ADMIN} — même simplification que {@code TenantController}/
 * {@code UserController} (pas encore de notion de rôle « Admin de tenant » auto-suffisante, ce
 * serait circulaire vu que c'est précisément ce que ce domaine construit).
 */
@RestController
@RequestMapping("/api/v1/tenants/{tenantId}")
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    public AuthorizationController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public List<RoleAssignmentResponse> list(@PathVariable UUID tenantId) {
        return authorizationService.getRoleAssignments(tenantId).stream()
                .map(RoleAssignmentResponse::from)
                .toList();
    }

    @PostMapping("/users/{userId}/roles/{roleCode}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public RoleAssignmentResponse assign(@PathVariable UUID tenantId,
                                          @PathVariable UUID userId,
                                          @PathVariable String roleCode) {
        RoleAssignment assignment = authorizationService.assignRole(tenantId, userId, parse(roleCode));
        return RoleAssignmentResponse.from(assignment);
    }

    @DeleteMapping("/users/{userId}/roles/{roleCode}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(@PathVariable UUID tenantId,
                        @PathVariable UUID userId,
                        @PathVariable String roleCode) {
        authorizationService.revokeRole(tenantId, userId, parse(roleCode));
    }

    private RoleCode parse(String value) {
        try {
            return RoleCode.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleCodeException(value);
        }
    }
}
