package com.klem.referentielapi.procedure.api.controller;

import com.klem.referentielapi.procedure.application.service.ProcedureMetierService;
import com.klem.referentielapi.procedure.domain.exception.UnknownTexteReglementaireException;
import com.klem.referentielapi.procedure.domain.model.ProcedureMetier;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProcedureMetierController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class ProcedureMetierControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProcedureMetierService service;

    @Test
    void propose_with_lecteur_role_is_forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/procedures")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LECTEUR")))
                        .contentType("application/json")
                        .content("""
                                {"nom": "Import véhicules", "code": "IMP-VEH"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void propose_with_editeur_role_returns_201() throws Exception {
        ProcedureMetier procedure = ProcedureMetier.propose("Import véhicules", "IMP-VEH", null, null, "editeur-1");
        when(service.propose(eq("Import véhicules"), eq("IMP-VEH"), eq(null), eq(null), org.mockito.ArgumentMatchers.any()))
                .thenReturn(procedure);

        mockMvc.perform(post("/api/v1/procedures")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EDITEUR")))
                        .contentType("application/json")
                        .content("""
                                {"nom": "Import véhicules", "code": "IMP-VEH"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("IMP-VEH"));
    }

    @Test
    void associateTexte_with_editeur_role_returns_201() throws Exception {
        UUID procedureId = UUID.randomUUID();
        UUID texteId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/procedures/{id}/textes/{texteId}", procedureId, texteId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EDITEUR"))))
                .andExpect(status().isCreated());
    }

    @Test
    void associateTexte_with_unknown_texte_returns_400() throws Exception {
        UUID procedureId = UUID.randomUUID();
        UUID unknownTexteId = UUID.randomUUID();
        doThrow(new UnknownTexteReglementaireException(unknownTexteId))
                .when(service).associateTexte(procedureId, unknownTexteId);

        mockMvc.perform(post("/api/v1/procedures/{id}/textes/{texteId}", procedureId, unknownTexteId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EDITEUR"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void dissociateTexte_with_lecteur_role_is_forbidden() throws Exception {
        UUID procedureId = UUID.randomUUID();
        UUID texteId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/procedures/{id}/textes/{texteId}", procedureId, texteId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LECTEUR"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void listTextes_with_lecteur_role_is_allowed() throws Exception {
        UUID procedureId = UUID.randomUUID();
        UUID texteId = UUID.randomUUID();
        when(service.listTexteIds(procedureId)).thenReturn(java.util.List.of(texteId));

        mockMvc.perform(get("/api/v1/procedures/{id}/textes", procedureId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LECTEUR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(texteId.toString()));
    }
}
