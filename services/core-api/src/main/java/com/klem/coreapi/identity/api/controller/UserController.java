package com.klem.coreapi.identity.api.controller;

import com.klem.coreapi.identity.api.request.InviteUserRequest;
import com.klem.coreapi.identity.api.response.TenantMembershipResponse;
import com.klem.coreapi.identity.api.response.UserProfileResponse;
import com.klem.coreapi.identity.application.service.IdentityService;
import com.klem.coreapi.identity.domain.model.TenantMembership;
import com.klem.coreapi.identity.domain.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller mince — logique dans {@link IdentityService}.
 * <p>
 * <b>Contrat JWT attendu</b> pour {@code GET /me} : claims OIDC standard {@code sub} (obligatoire),
 * {@code email} (obligatoire — utilisé pour la liaison d'invitation en attente), {@code name}
 * (optionnel, nom affiché). Comme pour {@code SecurityConfig} (claim {@code roles}), hypothèse à
 * valider dès qu'un royaume Keycloak réel existe.
 */
@RestController
public class UserController {

    private final IdentityService identityService;

    public UserController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @GetMapping("/api/v1/users/me")
    public UserProfileResponse me(@AuthenticationPrincipal Jwt jwt) {
        String keycloakSubject = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String displayName = jwt.getClaimAsString("name");

        User user = identityService.getOrProvisionCurrentUser(keycloakSubject, email, displayName);
        List<TenantMembership> memberships = identityService.getMemberships(user.getId());
        return UserProfileResponse.from(user, memberships);
    }

    @PostMapping("/api/v1/tenants/{tenantId}/users")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public TenantMembershipResponse invite(@PathVariable UUID tenantId,
                                            @Valid @RequestBody InviteUserRequest request) {
        TenantMembership membership = identityService.inviteUser(tenantId, request.email(), request.displayName());
        return TenantMembershipResponse.from(membership);
    }
}
