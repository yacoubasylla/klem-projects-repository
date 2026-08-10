package com.klem.coreapi.workflow.api.controller;

import com.klem.coreapi.authorization.domain.model.RoleAssignment;
import com.klem.coreapi.authorization.domain.model.RoleCode;
import com.klem.coreapi.identity.domain.model.TenantMembership;
import com.klem.coreapi.security.SecurityConfig;
import com.klem.coreapi.tenant.domain.model.Tenant;
import com.klem.coreapi.workflow.application.service.WorkflowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkflowController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class WorkflowControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkflowService workflowService;

    private static final String PLATFORM_ADMIN = "ROLE_PLATFORM_ADMIN";

    @Test
    void onboard_without_authentication_is_unauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/workflows/tenant-onboarding")
                        .contentType("application/json")
                        .content("""
                                {"tenantName": "X", "adminEmail": "a@klem.tech"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void onboard_without_platform_admin_role_is_forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/workflows/tenant-onboarding")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERATEUR")))
                        .contentType("application/json")
                        .content("""
                                {"tenantName": "X", "adminEmail": "a@klem.tech"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void onboard_with_invalid_email_is_rejected() throws Exception {
        mockMvc.perform(post("/api/v1/workflows/tenant-onboarding")
                        .with(jwt().authorities(new SimpleGrantedAuthority(PLATFORM_ADMIN)))
                        .contentType("application/json")
                        .content("""
                                {"tenantName": "Boutiki pilote", "adminEmail": "pas-un-email"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void onboard_with_valid_request_returns_201_with_composite_result() throws Exception {
        Tenant tenant = Tenant.create("Boutiki pilote", "commerce informel");
        TenantMembership membership = TenantMembership.invite(tenant.getId(), tenant.getId());
        RoleAssignment roleAssignment = RoleAssignment.grant(tenant.getId(), membership.getUserId(), RoleCode.ADMIN);
        WorkflowService.TenantOnboardingResult result =
                new WorkflowService.TenantOnboardingResult(tenant, membership, roleAssignment, "admin@klem.tech");

        when(workflowService.onboardTenant("Boutiki pilote", "commerce informel", "admin@klem.tech", "Admin Boutiki"))
                .thenReturn(result);

        mockMvc.perform(post("/api/v1/workflows/tenant-onboarding")
                        .with(jwt().authorities(new SimpleGrantedAuthority(PLATFORM_ADMIN)))
                        .contentType("application/json")
                        .content("""
                                {
                                  "tenantName": "Boutiki pilote",
                                  "tenantSector": "commerce informel",
                                  "adminEmail": "admin@klem.tech",
                                  "adminDisplayName": "Admin Boutiki"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tenantName").value("Boutiki pilote"))
                .andExpect(jsonPath("$.adminEmail").value("admin@klem.tech"))
                .andExpect(jsonPath("$.roleCode").value("ADMIN"))
                .andExpect(jsonPath("$.membershipStatus").value("INVITED"));
    }
}
