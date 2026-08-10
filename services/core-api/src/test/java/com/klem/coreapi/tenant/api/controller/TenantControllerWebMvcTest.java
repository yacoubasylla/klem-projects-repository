package com.klem.coreapi.tenant.api.controller;

import com.klem.coreapi.security.SecurityConfig;
import com.klem.coreapi.tenant.application.service.TenantService;
import com.klem.coreapi.tenant.domain.exception.TenantNotFoundException;
import com.klem.coreapi.tenant.domain.model.Tenant;
import com.klem.coreapi.tenant.domain.model.TenantStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Slice test (pas de Testcontainers, pas de démarrage complet du contexte) — vérifie le contrat
 * HTTP et l'application réelle des règles d'autorisation {@code @PreAuthorize} de
 * {@link TenantController}, {@link TenantService} mocké.
 */
@WebMvcTest(TenantController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class TenantControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TenantService tenantService;

    private static final String PLATFORM_ADMIN = "PLATFORM_ADMIN";

    @Test
    void create_without_authentication_is_unauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/tenants")
                        .contentType("application/json")
                        .content("""
                                {"name": "KLEM Trade-X pilote"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create_without_platform_admin_role_is_forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/tenants")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERATOR")))
                        .contentType("application/json")
                        .content("""
                                {"name": "KLEM Trade-X pilote"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_with_blank_name_is_rejected_with_validation_error() throws Exception {
        mockMvc.perform(post("/api/v1/tenants")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_" + PLATFORM_ADMIN)))
                        .contentType("application/json")
                        .content("""
                                {"name": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0].field").value("name"));
    }

    @Test
    void create_with_platform_admin_role_and_valid_body_returns_201() throws Exception {
        Tenant tenant = Tenant.create("KLEM Trade-X pilote", "commerce extérieur");
        when(tenantService.createTenant(eq("KLEM Trade-X pilote"), eq("commerce extérieur")))
                .thenReturn(tenant);

        mockMvc.perform(post("/api/v1/tenants")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_" + PLATFORM_ADMIN)))
                        .contentType("application/json")
                        .content("""
                                {"name": "KLEM Trade-X pilote", "sector": "commerce extérieur"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("KLEM Trade-X pilote"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void get_unknown_tenant_returns_404_with_standard_error_format() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(tenantService.getTenant(unknownId)).thenThrow(new TenantNotFoundException(unknownId));

        mockMvc.perform(get("/api/v1/tenants/{id}", unknownId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_" + PLATFORM_ADMIN))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void update_status_with_platform_admin_role_returns_200() throws Exception {
        Tenant tenant = Tenant.create("Boutiki pilote", "commerce informel");
        tenant.changeStatus(TenantStatus.ACTIVE);
        when(tenantService.changeStatus(eq(tenant.getId()), eq(TenantStatus.ACTIVE))).thenReturn(tenant);

        mockMvc.perform(patch("/api/v1/tenants/{id}/status", tenant.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_" + PLATFORM_ADMIN)))
                        .contentType("application/json")
                        .content("""
                                {"status": "ACTIVE"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}
