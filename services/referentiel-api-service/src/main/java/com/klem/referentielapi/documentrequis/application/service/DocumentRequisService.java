package com.klem.referentielapi.documentrequis.application.service;

import com.klem.referentielapi.documentrequis.application.port.DocumentRequisRepository;
import com.klem.referentielapi.documentrequis.domain.exception.DocumentRequisNotFoundException;
import com.klem.referentielapi.documentrequis.domain.model.DocumentRequis;
import com.klem.referentielapi.shared.domain.StatutPublication;
import com.klem.referentielapi.shared.domain.event.EntryProposedEvent;
import com.klem.referentielapi.shared.domain.event.EntryStatusChangedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Cas d'usage du domaine {@code documentrequis} — transactions définies ici, jamais dans le
 * controller.
 */
@Service
@Transactional(readOnly = true)
public class DocumentRequisService {

    private static final String AGGREGATE_TYPE = "documentRequis";

    private final DocumentRequisRepository repository;
    private final ApplicationEventPublisher events;

    public DocumentRequisService(DocumentRequisRepository repository, ApplicationEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @Transactional
    public DocumentRequis propose(String nom, String code, String description, String regleValidation, String createdBy) {
        DocumentRequis document = repository.save(DocumentRequis.propose(nom, code, description, regleValidation, createdBy));
        events.publishEvent(new EntryProposedEvent(
                UUID.randomUUID(), AGGREGATE_TYPE, document.getId(), document.getCode(), createdBy, Instant.now()));
        return document;
    }

    public DocumentRequis get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DocumentRequisNotFoundException(id));
    }

    public Page<DocumentRequis> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional
    public DocumentRequis changeStatus(UUID id, StatutPublication newStatus, String actorSubject) {
        DocumentRequis document = get(id);
        StatutPublication previousStatus = document.getStatut();
        document.changeStatus(newStatus, actorSubject);
        repository.save(document);
        events.publishEvent(new EntryStatusChangedEvent(
                UUID.randomUUID(), AGGREGATE_TYPE, document.getId(), document.getCode(),
                previousStatus, newStatus, actorSubject, Instant.now()));
        return document;
    }

    /**
     * Vérification d'existence exposée au domaine {@code operationcommerce} (jointure
     * {@code operation_document}) — même motif de lecture peer-à-peer étroite que sur les autres
     * domaines de ce service.
     */
    public boolean exists(UUID id) {
        return repository.existsById(id);
    }
}
