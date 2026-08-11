package com.klem.referentielapi.documentrequis.api.controller;

import com.klem.referentielapi.documentrequis.application.service.DocumentRequisService;
import com.klem.referentielapi.documentrequis.domain.exception.DocumentRequisNotFoundException;
import com.klem.referentielapi.documentrequis.domain.model.DocumentRequis;
import com.klem.referentielapi.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentRequisController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class DocumentRequisControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentRequisService service;

    @Test
    void propose_without_authentication_is_unauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/documents-requis")
                        .contentType("application/json")
                        .content("""
                                {"nom": "Certificat", "code": "CERT"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void propose_with_lecteur_role_is_forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/documents-requis")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LECTEUR")))
                        .contentType("application/json")
                        .content("""
                                {"nom": "Certificat", "code": "CERT"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void propose_with_blank_code_is_rejected_with_validation_error() throws Exception {
        mockMvc.perform(post("/api/v1/documents-requis")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType("application/json")
                        .content("""
                                {"nom": "Certificat", "code": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0].field").value("code"));
    }

    @Test
    void get_unknown_document_returns_404() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(service.get(unknownId)).thenThrow(new DocumentRequisNotFoundException(unknownId));

        mockMvc.perform(get("/api/v1/documents-requis/{id}", unknownId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LECTEUR"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void get_with_lecteur_role_is_allowed() throws Exception {
        DocumentRequis document = DocumentRequis.propose("Certificat", "CERT", null, null, "editeur-1");
        when(service.get(document.getId())).thenReturn(document);

        mockMvc.perform(get("/api/v1/documents-requis/{id}", document.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LECTEUR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("CERT"));
    }
}
