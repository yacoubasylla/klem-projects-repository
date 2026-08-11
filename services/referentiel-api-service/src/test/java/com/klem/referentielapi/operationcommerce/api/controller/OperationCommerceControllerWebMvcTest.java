package com.klem.referentielapi.operationcommerce.api.controller;

import com.klem.referentielapi.operationcommerce.application.service.OperationCommerceService;
import com.klem.referentielapi.operationcommerce.domain.exception.UnknownProcedureMetierException;
import com.klem.referentielapi.operationcommerce.domain.model.OperationCommerce;
import com.klem.referentielapi.operationcommerce.domain.model.TypeOperation;
import com.klem.referentielapi.security.SecurityConfig;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OperationCommerceController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class OperationCommerceControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OperationCommerceService service;

    @Test
    void propose_with_lecteur_role_is_forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/operations")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LECTEUR")))
                        .contentType("application/json")
                        .content("""
                                {"nom": "Import véhicules", "code": "IMP-VEH", "type": "IMPORT", "procedureId": "%s"}
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isForbidden());
    }

    @Test
    void propose_with_unknown_procedure_returns_400() throws Exception {
        UUID procedureId = UUID.randomUUID();
        doThrow(new UnknownProcedureMetierException(procedureId))
                .when(service).propose(any(), any(), any(), eq(procedureId), any());

        mockMvc.perform(post("/api/v1/operations")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EDITEUR")))
                        .contentType("application/json")
                        .content("""
                                {"nom": "Import véhicules", "code": "IMP-VEH", "type": "IMPORT", "procedureId": "%s"}
                                """.formatted(procedureId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void propose_with_editeur_role_and_valid_body_returns_201() throws Exception {
        UUID procedureId = UUID.randomUUID();
        OperationCommerce operation = OperationCommerce.propose("Import véhicules", "IMP-VEH", TypeOperation.IMPORT, procedureId, "editeur-1");
        when(service.propose(eq("Import véhicules"), eq("IMP-VEH"), eq(TypeOperation.IMPORT), eq(procedureId), any()))
                .thenReturn(operation);

        mockMvc.perform(post("/api/v1/operations")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EDITEUR")))
                        .contentType("application/json")
                        .content("""
                                {"nom": "Import véhicules", "code": "IMP-VEH", "type": "IMPORT", "procedureId": "%s"}
                                """.formatted(procedureId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("IMP-VEH"));
    }

    @Test
    void listDocumentsByCode_with_lecteur_role_is_allowed() throws Exception {
        UUID documentId = UUID.randomUUID();
        when(service.listDocumentIdsByCode("IMP-VEH")).thenReturn(List.of(documentId));

        mockMvc.perform(get("/api/v1/operations/{code}/documents", "IMP-VEH")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LECTEUR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(documentId.toString()));
    }

    @Test
    void associateDocument_with_editeur_role_returns_201() throws Exception {
        UUID operationId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/operations/{id}/documents/{documentId}", operationId, documentId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EDITEUR"))))
                .andExpect(status().isCreated());
    }

    @Test
    void dissociateDocument_with_lecteur_role_is_forbidden() throws Exception {
        UUID operationId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/operations/{id}/documents/{documentId}", operationId, documentId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LECTEUR"))))
                .andExpect(status().isForbidden());
    }
}
