package com.klem.referentielapi.operationcommerce.application.service;

import com.klem.referentielapi.documentrequis.application.service.DocumentRequisService;
import com.klem.referentielapi.operationcommerce.application.port.OperationCommerceRepository;
import com.klem.referentielapi.operationcommerce.application.port.OperationDocumentRepository;
import com.klem.referentielapi.operationcommerce.domain.exception.DocumentAlreadyAssociatedException;
import com.klem.referentielapi.operationcommerce.domain.exception.OperationCommerceNotFoundException;
import com.klem.referentielapi.operationcommerce.domain.exception.UnknownDocumentRequisException;
import com.klem.referentielapi.operationcommerce.domain.exception.UnknownProcedureMetierException;
import com.klem.referentielapi.operationcommerce.domain.model.OperationCommerce;
import com.klem.referentielapi.operationcommerce.domain.model.OperationDocument;
import com.klem.referentielapi.operationcommerce.domain.model.TypeOperation;
import com.klem.referentielapi.procedure.application.service.ProcedureMetierService;
import com.klem.referentielapi.shared.domain.StatutPublication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Cas d'usage du domaine {@code operationcommerce} — transactions définies ici, jamais dans le
 * controller. Dépend de {@code ProcedureMetierService#exists} et {@code DocumentRequisService#exists}
 * — lectures peer-à-peer étroites vers {@code procedure} et {@code documentrequis}, jamais leur
 * {@code domain.model}/{@code domain.exception} (voir {@code PackageBoundaryRulesTest}).
 */
@Service
@Transactional(readOnly = true)
public class OperationCommerceService {

    private final OperationCommerceRepository operationRepository;
    private final OperationDocumentRepository operationDocumentRepository;
    private final ProcedureMetierService procedureMetierService;
    private final DocumentRequisService documentRequisService;

    public OperationCommerceService(OperationCommerceRepository operationRepository,
                                     OperationDocumentRepository operationDocumentRepository,
                                     ProcedureMetierService procedureMetierService,
                                     DocumentRequisService documentRequisService) {
        this.operationRepository = operationRepository;
        this.operationDocumentRepository = operationDocumentRepository;
        this.procedureMetierService = procedureMetierService;
        this.documentRequisService = documentRequisService;
    }

    @Transactional
    public OperationCommerce propose(String nom, String code, TypeOperation type, UUID procedureId, String createdBy) {
        if (!procedureMetierService.exists(procedureId)) {
            throw new UnknownProcedureMetierException(procedureId);
        }
        return operationRepository.save(OperationCommerce.propose(nom, code, type, procedureId, createdBy));
    }

    public OperationCommerce get(UUID id) {
        return operationRepository.findById(id)
                .orElseThrow(() -> new OperationCommerceNotFoundException(id));
    }

    public OperationCommerce getByCode(String code) {
        return operationRepository.findByCode(code)
                .orElseThrow(() -> new OperationCommerceNotFoundException(code));
    }

    public Page<OperationCommerce> list(Pageable pageable) {
        return operationRepository.findAll(pageable);
    }

    @Transactional
    public OperationCommerce changeStatus(UUID id, StatutPublication newStatus, String actorSubject) {
        OperationCommerce operation = get(id);
        operation.changeStatus(newStatus, actorSubject);
        return operationRepository.save(operation);
    }

    @Transactional
    public void associateDocument(UUID operationId, UUID documentId, String conditionApplicabilite) {
        get(operationId); // valide l'existence
        if (!documentRequisService.exists(documentId)) {
            throw new UnknownDocumentRequisException(documentId);
        }
        if (operationDocumentRepository.findByOperationIdAndDocumentId(operationId, documentId).isPresent()) {
            throw new DocumentAlreadyAssociatedException(operationId, documentId);
        }
        operationDocumentRepository.save(OperationDocument.associate(operationId, documentId, conditionApplicabilite));
    }

    @Transactional
    public void dissociateDocument(UUID operationId, UUID documentId) {
        OperationDocument association = operationDocumentRepository.findByOperationIdAndDocumentId(operationId, documentId)
                .orElseThrow(() -> new UnknownDocumentRequisException(documentId));
        operationDocumentRepository.delete(association);
    }

    /**
     * Consommé par CLEAR-COMPLY via {@code GET /operations/{code}/documents} (spec §4.3) — le code
     * métier, pas l'UUID technique, est la clé de consultation publique de ce couple.
     */
    public List<UUID> listDocumentIdsByCode(String code) {
        OperationCommerce operation = getByCode(code);
        return operationDocumentRepository.findByOperationId(operation.getId()).stream()
                .map(OperationDocument::getDocumentId)
                .toList();
    }
}
