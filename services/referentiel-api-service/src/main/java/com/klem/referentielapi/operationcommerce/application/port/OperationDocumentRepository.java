package com.klem.referentielapi.operationcommerce.application.port;

import com.klem.referentielapi.operationcommerce.domain.model.OperationDocument;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OperationDocumentRepository {

    OperationDocument save(OperationDocument association);

    Optional<OperationDocument> findByOperationIdAndDocumentId(UUID operationId, UUID documentId);

    List<OperationDocument> findByOperationId(UUID operationId);

    void delete(OperationDocument association);
}
