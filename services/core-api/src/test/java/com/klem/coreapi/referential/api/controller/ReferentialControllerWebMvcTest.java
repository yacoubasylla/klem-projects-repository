package com.klem.coreapi.referential.api.controller;

import com.klem.coreapi.referential.application.service.ReferentialService;
import com.klem.coreapi.referential.domain.exception.ReferentialListNotFoundException;
import com.klem.coreapi.referential.domain.model.ReferentialEntry;
import com.klem.coreapi.referential.domain.model.ReferentialList;
import com.klem.coreapi.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReferentialController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class ReferentialControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReferentialService referentialService;

    @Test
    void get_without_authentication_is_unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/referentials/countries"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void get_with_any_authenticated_role_returns_list() throws Exception {
        ReferentialList countries = ReferentialList.create("countries", "Pays");
        ReferentialEntry ci = ReferentialEntry.create(countries.getId(), "CI", "Côte d'Ivoire", 1);
        when(referentialService.getList("countries")).thenReturn(countries);
        when(referentialService.getEntries(countries.getId())).thenReturn(List.of(ci));

        // Rôle OPERATOR, pas PLATFORM_ADMIN : contrairement à tenant/identity, referential
        // n'impose aucune restriction de rôle (voir ReferentialController, Javadoc).
        mockMvc.perform(get("/api/v1/referentials/countries").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("countries"))
                .andExpect(jsonPath("$.entries[0].code").value("CI"));
    }

    @Test
    void get_unknown_code_returns_404() throws Exception {
        when(referentialService.getList("inconnu")).thenThrow(new ReferentialListNotFoundException("inconnu"));

        mockMvc.perform(get("/api/v1/referentials/inconnu").with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }
}
