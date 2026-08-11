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
class OperationCommerceServiceTest {

    @Mock
    private OperationCommerceRepository operationRepository;

    @Mock
    private OperationDocumentRepository operationDocumentRepository;

    @Mock
    private ProcedureMetierService procedureMetierService;

    @Mock
    private DocumentRequisService documentRequisService;

    private OperationCommerceService service;

    @BeforeEach
    void setUp() {
        service = new OperationCommerceService(
                operationRepository, operationDocumentRepository, procedureMetierService, documentRequisService);
        lenient().when(operationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void propose_fails_when_procedure_does_not_exist() {
        UUID procedureId = UUID.randomUUID();
        when(procedureMetierService.exists(procedureId)).thenReturn(false);

        assertThatThrownBy(() -> service.propose("Import véhicules", "IMP-VEH", TypeOperation.IMPORT, procedureId, "editeur-1"))
                .isInstanceOf(UnknownProcedureMetierException.class);

        verify(operationRepository, never()).save(any());
    }

    @Test
    void propose_succeeds_when_procedure_exists() {
        UUID procedureId = UUID.randomUUID();
        when(procedureMetierService.exists(procedureId)).thenReturn(true);

        OperationCommerce operation = service.propose("Import véhicules", "IMP-VEH", TypeOperation.IMPORT, procedureId, "editeur-1");

        assertThat(operation.getCode()).isEqualTo("IMP-VEH");
        assertThat(operation.getProcedureId()).isEqualTo(procedureId);
    }

    @Test
    void get_throws_when_not_found() {
        UUID id = UUID.randomUUID();
        when(operationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(id)).isInstanceOf(OperationCommerceNotFoundException.class);
    }

    @Test
    void getByCode_throws_when_not_found() {
        when(operationRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByCode("UNKNOWN")).isInstanceOf(OperationCommerceNotFoundException.class);
    }

    @Test
    void associateDocument_fails_when_document_does_not_exist() {
        OperationCommerce operation = OperationCommerce.propose("Import", "IMP", TypeOperation.IMPORT, UUID.randomUUID(), "editeur-1");
        UUID documentId = UUID.randomUUID();
        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));
        when(documentRequisService.exists(documentId)).thenReturn(false);

        assertThatThrownBy(() -> service.associateDocument(operation.getId(), documentId, null))
                .isInstanceOf(UnknownDocumentRequisException.class);
    }

    @Test
    void associateDocument_fails_when_already_associated() {
        OperationCommerce operation = OperationCommerce.propose("Import", "IMP", TypeOperation.IMPORT, UUID.randomUUID(), "editeur-1");
        UUID documentId = UUID.randomUUID();
        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));
        when(documentRequisService.exists(documentId)).thenReturn(true);
        when(operationDocumentRepository.findByOperationIdAndDocumentId(operation.getId(), documentId))
                .thenReturn(Optional.of(OperationDocument.associate(operation.getId(), documentId, null)));

        assertThatThrownBy(() -> service.associateDocument(operation.getId(), documentId, null))
                .isInstanceOf(DocumentAlreadyAssociatedException.class);
    }

    @Test
    void associateDocument_succeeds_when_operation_and_document_exist_and_not_yet_associated() {
        OperationCommerce operation = OperationCommerce.propose("Import", "IMP", TypeOperation.IMPORT, UUID.randomUUID(), "editeur-1");
        UUID documentId = UUID.randomUUID();
        when(operationRepository.findById(operation.getId())).thenReturn(Optional.of(operation));
        when(documentRequisService.exists(documentId)).thenReturn(true);
        when(operationDocumentRepository.findByOperationIdAndDocumentId(operation.getId(), documentId))
                .thenReturn(Optional.empty());

        service.associateDocument(operation.getId(), documentId, "valeur CAF > 2M FCFA");

        verify(operationDocumentRepository).save(any(OperationDocument.class));
    }

    @Test
    void listDocumentIdsByCode_resolves_operation_by_code_then_lists_documents() {
        OperationCommerce operation = OperationCommerce.propose("Import", "IMP", TypeOperation.IMPORT, UUID.randomUUID(), "editeur-1");
        UUID documentId = UUID.randomUUID();
        when(operationRepository.findByCode("IMP")).thenReturn(Optional.of(operation));
        when(operationDocumentRepository.findByOperationId(operation.getId()))
                .thenReturn(List.of(OperationDocument.associate(operation.getId(), documentId, null)));

        List<UUID> documentIds = service.listDocumentIdsByCode("IMP");

        assertThat(documentIds).containsExactly(documentId);
    }
}
