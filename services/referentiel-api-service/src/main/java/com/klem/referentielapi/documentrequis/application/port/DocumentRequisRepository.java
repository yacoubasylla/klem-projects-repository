package com.klem.referentielapi.documentrequis.application.port;

import com.klem.referentielapi.documentrequis.domain.model.DocumentRequis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface DocumentRequisRepository {

    DocumentRequis save(DocumentRequis document);

    Optional<DocumentRequis> findById(UUID id);

    Page<DocumentRequis> findAll(Pageable pageable);

    boolean existsById(UUID id);
}
