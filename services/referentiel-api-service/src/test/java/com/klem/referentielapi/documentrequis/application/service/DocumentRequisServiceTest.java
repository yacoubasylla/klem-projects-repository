package com.klem.referentielapi.documentrequis.application.service;

import com.klem.referentielapi.documentrequis.application.port.DocumentRequisRepository;
import com.klem.referentielapi.documentrequis.domain.exception.DocumentRequisNotFoundException;
import com.klem.referentielapi.documentrequis.domain.model.DocumentRequis;
import com.klem.referentielapi.shared.domain.InvalidStatutTransitionException;
import com.klem.referentielapi.shared.domain.StatutPublication;
import com.klem.referentielapi.shared.domain.event.EntryProposedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentRequisServiceTest {

    @Mock
    private DocumentRequisRepository repository;

    @Mock
    private ApplicationEventPublisher events;

    private DocumentRequisService service;

    @BeforeEach
    void setUp() {
        service = new DocumentRequisService(repository, events);
        lenient().when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void propose_creates_document_in_proposee_status_and_publishes_event() {
        DocumentRequis document = service.propose("Certificat d'origine", "CERT-ORIG", "desc", "regle", "editeur-1");

        assertThat(document.getStatut()).isEqualTo(StatutPublication.PROPOSEE);
        assertThat(document.getCode()).isEqualTo("CERT-ORIG");
        verify(events).publishEvent(any(EntryProposedEvent.class));
    }

    @Test
    void get_throws_when_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(id)).isInstanceOf(DocumentRequisNotFoundException.class);
    }

    @Test
    void changeStatus_rejects_invalid_transition() {
        DocumentRequis document = DocumentRequis.propose("Nom", "CODE", null, null, "editeur-1");
        when(repository.findById(document.getId())).thenReturn(Optional.of(document));

        assertThatThrownBy(() -> service.changeStatus(document.getId(), StatutPublication.PUBLIEE, "admin-1"))
                .isInstanceOf(InvalidStatutTransitionException.class);
    }

    @Test
    void exists_reflects_repository_result() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThat(service.exists(id)).isFalse();
    }
}
