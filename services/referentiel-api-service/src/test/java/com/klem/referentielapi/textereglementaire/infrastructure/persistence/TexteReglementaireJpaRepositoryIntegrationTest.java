package com.klem.referentielapi.textereglementaire.infrastructure.persistence;

import com.klem.referentielapi.textereglementaire.application.port.TexteReglementaireRepository;
import com.klem.referentielapi.textereglementaire.domain.model.TexteReglementaire;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Vérifie la migration Flyway {@code V1__create_texte_reglementaire_table.sql} et le mapping JPA
 * réel, contre un PostgreSQL réel (Testcontainers) — pas H2, pour rester fidèle au moteur de
 * production ({@code KLEM_MASTER_SYSTEM_DIRECTIVE.md} §18).
 * <p>
 * <b>Non exécuté avec succès dans ce bac à sable</b> : le client Docker local expose l'API 1.32,
 * Testcontainers exige au minimum 1.40 — confirmé non spécifique à ce code (même échec sur
 * {@code transit-ops-service}/{@code core-api}, voir leurs README respectifs). À faire tourner
 * réellement dès qu'un environnement Docker à jour est disponible.
 */
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TexteReglementaireJpaRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TexteReglementaireRepository repository;

    @Test
    void save_and_findById_round_trip() {
        TexteReglementaire texte = TexteReglementaire.propose(
                "Note de procédure import véhicules", "circulaire", LocalDate.of(2026, 1, 15),
                "REF-2026-001", "import", "https://douanes.ci/notes/2026-001", "editeur-1");

        repository.save(texte);

        Optional<TexteReglementaire> found = repository.findById(texte.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitre()).isEqualTo("Note de procédure import véhicules");
        assertThat(found.get().getReference()).isEqualTo("REF-2026-001");
    }

    @Test
    void findById_returns_empty_when_absent() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    void existsById_reflects_persisted_state() {
        TexteReglementaire texte = TexteReglementaire.propose(
                "Titre", "loi", null, null, null, null, "editeur-1");
        repository.save(texte);

        assertThat(repository.existsById(texte.getId())).isTrue();
        assertThat(repository.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    void findAll_paginates() {
        for (int i = 0; i < 3; i++) {
            repository.save(TexteReglementaire.propose(
                    "Titre " + i, "loi", null, null, null, null, "editeur-1"));
        }

        assertThat(repository.findAll(PageRequest.of(0, 2)).getContent()).hasSize(2);
        assertThat(repository.findAll(PageRequest.of(0, 2)).getTotalElements()).isEqualTo(3);
    }
}
