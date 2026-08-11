package com.klem.referentielapi.textereglementaire.api.controller;

import com.klem.referentielapi.security.SecurityConfig;
import com.klem.referentielapi.shared.domain.StatutPublication;
import com.klem.referentielapi.textereglementaire.application.service.TexteReglementaireService;
import com.klem.referentielapi.textereglementaire.domain.exception.TexteReglementaireNotFoundException;
import com.klem.referentielapi.textereglementaire.domain.model.TexteReglementaire;
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
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Slice test — vérifie le contrat HTTP et l'application réelle des règles {@code @PreAuthorize} de
 * {@link TexteReglementaireController}, {@link TexteReglementaireService} mocké.
 */
@WebMvcTest(TexteReglementaireController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class TexteReglementaireControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TexteReglementaireService service;

    @Test
    void propose_without_authentication_is_unauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/textes-reglementaires")
                        .contentType("application/json")
                        .content("""
                                {"titre": "Note", "type": "circulaire"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void propose_with_lecteur_role_is_forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/textes-reglementaires")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LECTEUR")))
                        .contentType("application/json")
                        .content("""
                                {"titre": "Note", "type": "circulaire"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void propose_with_blank_titre_is_rejected_with_validation_error() throws Exception {
        mockMvc.perform(post("/api/v1/textes-reglementaires")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EDITEUR")))
                        .contentType("application/json")
                        .content("""
                                {"titre": "", "type": "circulaire"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0].field").value("titre"));
    }

    @Test
    void propose_with_editeur_role_and_valid_body_returns_201() throws Exception {
        TexteReglementaire texte = TexteReglementaire.propose(
                "Note de procédure import", "circulaire", null, null, null, null, "editeur-jwt-subject");
        when(service.propose(eq("Note de procédure import"), eq("circulaire"), isNull(), isNull(), isNull(), isNull(), any()))
                .thenReturn(texte);

        mockMvc.perform(post("/api/v1/textes-reglementaires")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EDITEUR")).jwt(j -> j.subject("editeur-jwt-subject")))
                        .contentType("application/json")
                        .content("""
                                {"titre": "Note de procédure import", "type": "circulaire"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titre").value("Note de procédure import"))
                .andExpect(jsonPath("$.statut").value("PROPOSEE"));
    }

    @Test
    void get_unknown_texte_returns_404_with_standard_error_format() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(service.get(unknownId)).thenThrow(new TexteReglementaireNotFoundException(unknownId));

        mockMvc.perform(get("/api/v1/textes-reglementaires/{id}", unknownId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LECTEUR"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void get_with_lecteur_role_is_allowed() throws Exception {
        TexteReglementaire texte = TexteReglementaire.propose(
                "Titre", "loi", null, null, null, null, "editeur-1");
        when(service.get(texte.getId())).thenReturn(texte);

        mockMvc.perform(get("/api/v1/textes-reglementaires/{id}", texte.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LECTEUR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Titre"));
    }

    @Test
    void updateStatut_with_editeur_role_returns_200() throws Exception {
        TexteReglementaire texte = TexteReglementaire.propose(
                "Titre", "loi", null, null, null, null, "editeur-1");
        texte.changeStatus(StatutPublication.EN_REVISION, "editeur-1");
        when(service.changeStatus(eq(texte.getId()), eq(StatutPublication.EN_REVISION), any()))
                .thenReturn(texte);

        mockMvc.perform(patch("/api/v1/textes-reglementaires/{id}/statut", texte.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EDITEUR")))
                        .contentType("application/json")
                        .content("""
                                {"statut": "EN_REVISION"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("EN_REVISION"));
    }

    @Test
    void updateStatut_with_lecteur_role_is_forbidden() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(patch("/api/v1/textes-reglementaires/{id}/statut", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LECTEUR")))
                        .contentType("application/json")
                        .content("""
                                {"statut": "EN_REVISION"}
                                """))
                .andExpect(status().isForbidden());
    }
}
