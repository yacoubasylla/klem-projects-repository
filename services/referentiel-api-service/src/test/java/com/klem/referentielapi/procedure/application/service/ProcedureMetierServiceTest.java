package com.klem.referentielapi.procedure.application.service;

import com.klem.referentielapi.procedure.application.port.ProcedureMetierRepository;
import com.klem.referentielapi.procedure.application.port.ProcedureTexteRepository;
import com.klem.referentielapi.procedure.domain.exception.ProcedureMetierNotFoundException;
import com.klem.referentielapi.procedure.domain.exception.TexteAlreadyAssociatedException;
import com.klem.referentielapi.procedure.domain.exception.UnknownTexteReglementaireException;
import com.klem.referentielapi.procedure.domain.model.ProcedureMetier;
import com.klem.referentielapi.procedure.domain.model.ProcedureTexte;
import com.klem.referentielapi.textereglementaire.application.service.TexteReglementaireService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcedureMetierServiceTest {

    @Mock
    private ProcedureMetierRepository procedureRepository;

    @Mock
    private ProcedureTexteRepository procedureTexteRepository;

    @Mock
    private TexteReglementaireService texteReglementaireService;

    private ProcedureMetierService service;

    @BeforeEach
    void setUp() {
        service = new ProcedureMetierService(procedureRepository, procedureTexteRepository, texteReglementaireService);
        lenient().when(procedureRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void propose_creates_procedure_in_proposee_status() {
        ProcedureMetier procedure = service.propose("Import véhicules", "IMP-VEH", "desc", "Douanes, GUCE", "editeur-1");

        assertThat(procedure.getNom()).isEqualTo("Import véhicules");
        assertThat(procedure.getCode()).isEqualTo("IMP-VEH");
    }

    @Test
    void get_throws_when_not_found() {
        UUID id = UUID.randomUUID();
        when(procedureRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(id)).isInstanceOf(ProcedureMetierNotFoundException.class);
    }

    @Test
    void associateTexte_fails_when_texte_does_not_exist() {
        ProcedureMetier procedure = ProcedureMetier.propose("Import", "IMP", null, null, "editeur-1");
        when(procedureRepository.findById(procedure.getId())).thenReturn(Optional.of(procedure));
        UUID unknownTexteId = UUID.randomUUID();
        when(texteReglementaireService.exists(unknownTexteId)).thenReturn(false);

        assertThatThrownBy(() -> service.associateTexte(procedure.getId(), unknownTexteId))
                .isInstanceOf(UnknownTexteReglementaireException.class);

        verify(procedureTexteRepository, never()).save(any());
    }

    @Test
    void associateTexte_fails_when_already_associated() {
        ProcedureMetier procedure = ProcedureMetier.propose("Import", "IMP", null, null, "editeur-1");
        UUID texteId = UUID.randomUUID();
        when(procedureRepository.findById(procedure.getId())).thenReturn(Optional.of(procedure));
        when(texteReglementaireService.exists(texteId)).thenReturn(true);
        when(procedureTexteRepository.findByProcedureIdAndTexteId(procedure.getId(), texteId))
                .thenReturn(Optional.of(ProcedureTexte.associate(procedure.getId(), texteId)));

        assertThatThrownBy(() -> service.associateTexte(procedure.getId(), texteId))
                .isInstanceOf(TexteAlreadyAssociatedException.class);
    }

    @Test
    void associateTexte_succeeds_when_procedure_and_texte_exist_and_not_yet_associated() {
        ProcedureMetier procedure = ProcedureMetier.propose("Import", "IMP", null, null, "editeur-1");
        UUID texteId = UUID.randomUUID();
        when(procedureRepository.findById(procedure.getId())).thenReturn(Optional.of(procedure));
        when(texteReglementaireService.exists(texteId)).thenReturn(true);
        when(procedureTexteRepository.findByProcedureIdAndTexteId(procedure.getId(), texteId))
                .thenReturn(Optional.empty());

        service.associateTexte(procedure.getId(), texteId);

        verify(procedureTexteRepository).save(any(ProcedureTexte.class));
    }

    @Test
    void listTexteIds_returns_associated_texte_ids() {
        ProcedureMetier procedure = ProcedureMetier.propose("Import", "IMP", null, null, "editeur-1");
        UUID texteId = UUID.randomUUID();
        when(procedureRepository.findById(procedure.getId())).thenReturn(Optional.of(procedure));
        when(procedureTexteRepository.findByProcedureId(procedure.getId()))
                .thenReturn(List.of(ProcedureTexte.associate(procedure.getId(), texteId)));

        List<UUID> texteIds = service.listTexteIds(procedure.getId());

        assertThat(texteIds).containsExactly(texteId);
    }

    @Test
    void exists_reflects_repository_result() {
        UUID id = UUID.randomUUID();
        when(procedureRepository.existsById(id)).thenReturn(true);

        assertThat(service.exists(id)).isTrue();
    }
}
