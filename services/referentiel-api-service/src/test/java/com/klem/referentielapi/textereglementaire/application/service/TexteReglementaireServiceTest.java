package com.klem.referentielapi.textereglementaire.application.service;

import com.klem.referentielapi.shared.domain.InvalidStatutTransitionException;
import com.klem.referentielapi.shared.domain.StatutPublication;
import com.klem.referentielapi.shared.domain.event.EntryProposedEvent;
import com.klem.referentielapi.shared.domain.event.EntryStatusChangedEvent;
import com.klem.referentielapi.textereglementaire.application.port.TexteReglementaireRepository;
import com.klem.referentielapi.textereglementaire.domain.exception.TexteReglementaireNotFoundException;
import com.klem.referentielapi.textereglementaire.domain.model.TexteReglementaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TexteReglementaireServiceTest {

    @Mock
    private TexteReglementaireRepository repository;

    @Mock
    private ApplicationEventPublisher events;

    private TexteReglementaireService service;

    @BeforeEach
    void setUp() {
        service = new TexteReglementaireService(repository, events);
        lenient().when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void propose_creates_texte_in_proposee_status_and_publishes_event() {
        TexteReglementaire texte = service.propose(
                "Note de procédure import véhicules", "circulaire", LocalDate.of(2026, 1, 15),
                "REF-2026-001", "import", "https://douanes.ci/notes/2026-001", "editeur-1");

        assertThat(texte.getStatut()).isEqualTo(StatutPublication.PROPOSEE);
        assertThat(texte.getCreatedBy()).isEqualTo("editeur-1");
        assertThat(texte.getValidatedBy()).isNull();
        verify(events).publishEvent(any(EntryProposedEvent.class));
    }

    @Test
    void get_throws_when_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(id))
                .isInstanceOf(TexteReglementaireNotFoundException.class);
    }

    @Test
    void changeStatus_from_proposee_to_en_revision_succeeds_and_publishes_event() {
        TexteReglementaire texte = TexteReglementaire.propose(
                "Titre", "loi", null, null, null, null, "editeur-1");
        when(repository.findById(texte.getId())).thenReturn(Optional.of(texte));

        TexteReglementaire updated = service.changeStatus(texte.getId(), StatutPublication.EN_REVISION, "editeur-2");

        assertThat(updated.getStatut()).isEqualTo(StatutPublication.EN_REVISION);
        assertThat(updated.getValidatedBy()).isNull();
        verify(events).publishEvent(any(EntryStatusChangedEvent.class));
    }

    @Test
    void changeStatus_to_publiee_stamps_validator() {
        TexteReglementaire texte = TexteReglementaire.propose(
                "Titre", "loi", null, null, null, null, "editeur-1");
        texte.changeStatus(StatutPublication.EN_REVISION, "editeur-1");
        when(repository.findById(texte.getId())).thenReturn(Optional.of(texte));

        TexteReglementaire updated = service.changeStatus(texte.getId(), StatutPublication.PUBLIEE, "admin-1");

        assertThat(updated.getStatut()).isEqualTo(StatutPublication.PUBLIEE);
        assertThat(updated.getValidatedBy()).isEqualTo("admin-1");
        assertThat(updated.getValidatedAt()).isNotNull();
    }

    @Test
    void changeStatus_rejects_invalid_transition_from_proposee_to_publiee() {
        TexteReglementaire texte = TexteReglementaire.propose(
                "Titre", "loi", null, null, null, null, "editeur-1");
        when(repository.findById(texte.getId())).thenReturn(Optional.of(texte));

        assertThatThrownBy(() -> service.changeStatus(texte.getId(), StatutPublication.PUBLIEE, "admin-1"))
                .isInstanceOf(InvalidStatutTransitionException.class);
    }

    @Test
    void exists_reflects_repository_result() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        assertThat(service.exists(id)).isTrue();
    }
}
