package com.klem.coreapi.audit.api.controller;

import com.klem.coreapi.audit.application.service.AuditService;
import com.klem.coreapi.audit.domain.model.AuditEntry;
import com.klem.coreapi.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuditController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class AuditControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditService auditService;

    private final UUID tenantId = UUID.randomUUID();

    @Test
    void list_without_authentication_is_unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/tenants/{tenantId}/audit-events", tenantId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void list_without_platform_admin_role_is_forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/tenants/{tenantId}/audit-events", tenantId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERATEUR"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void list_with_platform_admin_role_returns_paginated_entries() throws Exception {
        AuditEntry entry = AuditEntry.capture(UUID.randomUUID(), "tenant.created", tenantId, tenantId, Instant.now(), "{\"name\":\"X\"}");
        when(auditService.getEntries(any(), any()))
                .thenReturn(new PageImpl<>(List.of(entry), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/tenants/{tenantId}/audit-events", tenantId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PLATFORM_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].eventType").value("tenant.created"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
