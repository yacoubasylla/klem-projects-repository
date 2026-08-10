package com.klem.coreapi.authorization.api.controller;

import com.klem.coreapi.authorization.application.service.AuthorizationService;
import com.klem.coreapi.authorization.domain.exception.RoleAssignmentNotFoundException;
import com.klem.coreapi.authorization.domain.exception.UserNotMemberOfTenantException;
import com.klem.coreapi.authorization.domain.model.RoleAssignment;
import com.klem.coreapi.authorization.domain.model.RoleCode;
import com.klem.coreapi.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorizationController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class AuthorizationControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorizationService authorizationService;

    private static final String PLATFORM_ADMIN = "ROLE_PLATFORM_ADMIN";

    private final UUID tenantId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void list_without_authentication_is_unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/tenants/{tenantId}/roles", tenantId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void list_without_platform_admin_role_is_forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/tenants/{tenantId}/roles", tenantId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERATEUR"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void list_with_platform_admin_role_returns_assignments() throws Exception {
        RoleAssignment assignment = RoleAssignment.grant(tenantId, userId, RoleCode.OPERATEUR);
        when(authorizationService.getRoleAssignments(tenantId)).thenReturn(List.of(assignment));

        mockMvc.perform(get("/api/v1/tenants/{tenantId}/roles", tenantId)
                        .with(jwt().authorities(new SimpleGrantedAuthority(PLATFORM_ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roleCode").value("OPERATEUR"));
    }

    @Test
    void assign_with_invalid_role_code_returns_400() throws Exception {
        mockMvc.perform(post("/api/v1/tenants/{tenantId}/users/{userId}/roles/{roleCode}", tenantId, userId, "PAS_UN_ROLE")
                        .with(jwt().authorities(new SimpleGrantedAuthority(PLATFORM_ADMIN))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void assign_when_user_not_member_returns_400() throws Exception {
        when(authorizationService.assignRole(eq(tenantId), eq(userId), eq(RoleCode.OPERATEUR)))
                .thenThrow(new UserNotMemberOfTenantException(userId, tenantId));

        mockMvc.perform(post("/api/v1/tenants/{tenantId}/users/{userId}/roles/{roleCode}", tenantId, userId, "OPERATEUR")
                        .with(jwt().authorities(new SimpleGrantedAuthority(PLATFORM_ADMIN))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void assign_with_valid_role_returns_201() throws Exception {
        RoleAssignment assignment = RoleAssignment.grant(tenantId, userId, RoleCode.CHAUFFEUR);
        when(authorizationService.assignRole(eq(tenantId), eq(userId), eq(RoleCode.CHAUFFEUR))).thenReturn(assignment);

        mockMvc.perform(post("/api/v1/tenants/{tenantId}/users/{userId}/roles/{roleCode}", tenantId, userId, "CHAUFFEUR")
                        .with(jwt().authorities(new SimpleGrantedAuthority(PLATFORM_ADMIN))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roleCode").value("CHAUFFEUR"));
    }

    @Test
    void revoke_unknown_assignment_returns_404() throws Exception {
        org.mockito.Mockito.doThrow(new RoleAssignmentNotFoundException(userId, tenantId, RoleCode.OPERATEUR))
                .when(authorizationService).revokeRole(tenantId, userId, RoleCode.OPERATEUR);

        mockMvc.perform(delete("/api/v1/tenants/{tenantId}/users/{userId}/roles/{roleCode}", tenantId, userId, "OPERATEUR")
                        .with(jwt().authorities(new SimpleGrantedAuthority(PLATFORM_ADMIN))))
                .andExpect(status().isNotFound());
    }

    @Test
    void revoke_existing_assignment_returns_204() throws Exception {
        mockMvc.perform(delete("/api/v1/tenants/{tenantId}/users/{userId}/roles/{roleCode}", tenantId, userId, "OPERATEUR")
                        .with(jwt().authorities(new SimpleGrantedAuthority(PLATFORM_ADMIN))))
                .andExpect(status().isNoContent());
    }
}
