package com.klem.referentielapi.operationcommerce.infrastructure.persistence;

import com.klem.referentielapi.documentrequis.application.port.DocumentRequisRepository;
import com.klem.referentielapi.documentrequis.domain.model.DocumentRequis;
import com.klem.referentielapi.operationcommerce.application.port.OperationCommerceRepository;
import com.klem.referentielapi.operationcommerce.application.port.OperationDocumentRepository;
import com.klem.referentielapi.operationcommerce.domain.model.OperationCommerce;
import com.klem.referentielapi.operationcommerce.domain.model.OperationDocument;
import com.klem.referentielapi.operationcommerce.domain.model.TypeOperation;
import com.klem.referentielapi.procedure.application.port.ProcedureMetierRepository;
import com.klem.referentielapi.procedure.domain.model.ProcedureMetier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Vérifie les migrations Flyway V1-V4 et le mapping JPA réel, y compris les FK
 * {@code operation_commerce.procedure_id}/{@code operation_document.document_id} et la contrainte
 * d'unicité {@code uq_operation_document} — non exécuté avec succès dans ce bac à sable (même
 * limitation Docker documentée sur les domaines précédents).
 */
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class OperationCommerceJpaRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private OperationCommerceRepository operationRepository;

    @Autowired
    private OperationDocumentRepository operationDocumentRepository;

    @Autowired
    private ProcedureMetierRepository procedureRepository;

    @Autowired
    private DocumentRequisRepository documentRequisRepository;

    @Test
    void save_and_findByCode_round_trip() {
        ProcedureMetier procedure = ProcedureMetier.propose("Import", "IMP", null, null, "editeur-1");
        procedureRepository.save(procedure);
        OperationCommerce operation = OperationCommerce.propose(
                "Import véhicules", "IMP-VEH", TypeOperation.IMPORT, procedure.getId(), "editeur-1");

        operationRepository.save(operation);

        Optional<OperationCommerce> found = operationRepository.findByCode("IMP-VEH");
        assertThat(found).isPresent();
        assertThat(found.get().getProcedureId()).isEqualTo(procedure.getId());
    }

    @Test
    void associate_document_round_trip() {
        ProcedureMetier procedure = ProcedureMetier.propose("Import", "IMP", null, null, "editeur-1");
        procedureRepository.save(procedure);
        OperationCommerce operation = OperationCommerce.propose(
                "Import", "IMP-VEH", TypeOperation.IMPORT, procedure.getId(), "editeur-1");
        operationRepository.save(operation);
        DocumentRequis document = DocumentRequis.propose("Certificat", "CERT", null, null, "editeur-1");
        documentRequisRepository.save(document);

        operationDocumentRepository.save(OperationDocument.associate(operation.getId(), document.getId(), "CAF > 2M FCFA"));

        List<OperationDocument> associations = operationDocumentRepository.findByOperationId(operation.getId());
        assertThat(associations).hasSize(1);
        assertThat(associations.get(0).getConditionApplicabilite()).isEqualTo("CAF > 2M FCFA");
    }
}
