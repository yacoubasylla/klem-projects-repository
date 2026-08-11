package com.klem.referentielapi.documentrequis.infrastructure.persistence;

import com.klem.referentielapi.documentrequis.application.port.DocumentRequisRepository;
import com.klem.referentielapi.documentrequis.domain.model.DocumentRequis;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Vérifie la migration Flyway V3 et le mapping JPA réel — non exécuté avec succès dans ce bac à
 * sable (même limitation Docker documentée sur les domaines précédents).
 */
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class DocumentRequisJpaRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private DocumentRequisRepository repository;

    @Test
    void save_and_findById_round_trip() {
        DocumentRequis document = DocumentRequis.propose("Certificat d'origine", "CERT-ORIG", "desc", "regle", "editeur-1");

        repository.save(document);

        Optional<DocumentRequis> found = repository.findById(document.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getNom()).isEqualTo("Certificat d'origine");
    }
}
