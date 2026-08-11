package com.klem.referentielapi.operationcommerce.infrastructure.persistence;

import com.klem.referentielapi.operationcommerce.application.port.OperationDocumentRepository;
import com.klem.referentielapi.operationcommerce.domain.model.OperationDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface OperationDocumentJpaRepository extends JpaRepository<OperationDocument, UUID>, OperationDocumentRepository {

    @Override
    Optional<OperationDocument> findByOperationIdAndDocumentId(UUID operationId, UUID documentId);

    @Override
    List<OperationDocument> findByOperationId(UUID operationId);
}
