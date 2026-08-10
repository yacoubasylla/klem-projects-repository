package com.klem.coreapi.identity.api.controller;

import com.klem.coreapi.identity.application.service.IdentityService;
import com.klem.coreapi.identity.domain.exception.UnknownTenantException;
import com.klem.coreapi.identity.domain.model.TenantMembership;
import com.klem.coreapi.identity.domain.model.User;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class UserControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IdentityService identityService;

    private static final String PLATFORM_ADMIN = "PLATFORM_ADMIN";

    @Test
    void me_without_authentication_is_unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_returns_profile_with_memberships_for_any_authenticated_user() throws Exception {
        User user = User.provisioned("kc-sub-1", "user@klem.tech", "Test User");
        TenantMembership membership = TenantMembership.invite(user.getId(), UUID.randomUUID());
        membership.activate();

        when(identityService.getOrProvisionCurrentUser(eq("kc-sub-1"), eq("user@klem.tech"), eq("Test User")))
                .thenReturn(user);
        when(identityService.getMemberships(user.getId())).thenReturn(List.of(membership));

        mockMvc.perform(get("/api/v1/users/me")
                        .with(jwt().jwt(builder -> builder
                                .subject("kc-sub-1")
                                .claim("email", "user@klem.tech")
                                .claim("name", "Test User"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@klem.tech"))
                .andExpect(jsonPath("$.tenants[0].status").value("ACTIVE"));
    }

    @Test
    void invite_without_platform_admin_role_is_forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/tenants/{tenantId}/users", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERATOR")))
                        .contentType("application/json")
                        .content("""
                                {"email": "invite@klem.tech"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void invite_with_invalid_email_is_rejected() throws Exception {
        mockMvc.perform(post("/api/v1/tenants/{tenantId}/users", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_" + PLATFORM_ADMIN)))
                        .contentType("application/json")
                        .content("""
                                {"email": "pas-un-email"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void invite_unknown_tenant_returns_404() throws Exception {
        UUID tenantId = UUID.randomUUID();
        when(identityService.inviteUser(eq(tenantId), any(), any()))
                .thenThrow(new UnknownTenantException(tenantId));

        mockMvc.perform(post("/api/v1/tenants/{tenantId}/users", tenantId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_" + PLATFORM_ADMIN)))
                        .contentType("application/json")
                        .content("""
                                {"email": "invite@klem.tech"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void invite_with_platform_admin_role_returns_201() throws Exception {
        UUID tenantId = UUID.randomUUID();
        TenantMembership membership = TenantMembership.invite(UUID.randomUUID(), tenantId);
        when(identityService.inviteUser(eq(tenantId), eq("invite@klem.tech"), any())).thenReturn(membership);

        mockMvc.perform(post("/api/v1/tenants/{tenantId}/users", tenantId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_" + PLATFORM_ADMIN)))
                        .contentType("application/json")
                        .content("""
                                {"email": "invite@klem.tech", "displayName": "Invité"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("INVITED"));
    }
}
