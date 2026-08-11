package com.klem.referentielapi.documentrequis.application.service;

import com.klem.referentielapi.documentrequis.application.port.DocumentRequisRepository;
import com.klem.referentielapi.documentrequis.domain.exception.DocumentRequisNotFoundException;
import com.klem.referentielapi.documentrequis.domain.model.DocumentRequis;
import com.klem.referentielapi.shared.domain.StatutPublication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Cas d'usage du domaine {@code documentrequis} — transactions définies ici, jamais dans le
 * controller.
 */
@Service
@Transactional(readOnly = true)
public class DocumentRequisService {

    private final DocumentRequisRepository repository;

    public DocumentRequisService(DocumentRequisRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DocumentRequis propose(String nom, String code, String description, String regleValidation, String createdBy) {
        return repository.save(DocumentRequis.propose(nom, code, description, regleValidation, createdBy));
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
        document.changeStatus(newStatus, actorSubject);
        return repository.save(document);
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
