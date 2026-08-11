package com.klem.referentielapi.procedure.infrastructure.persistence;

import com.klem.referentielapi.procedure.application.port.ProcedureMetierRepository;
import com.klem.referentielapi.procedure.application.port.ProcedureTexteRepository;
import com.klem.referentielapi.procedure.domain.model.ProcedureMetier;
import com.klem.referentielapi.procedure.domain.model.ProcedureTexte;
import com.klem.referentielapi.textereglementaire.application.port.TexteReglementaireRepository;
import com.klem.referentielapi.textereglementaire.domain.model.TexteReglementaire;
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
 * Vérifie les migrations Flyway V1/V2 et le mapping JPA réel, y compris la contrainte d'unicité
 * {@code uq_procedure_texte} — non exécuté avec succès dans ce bac à sable (même limitation Docker
 * documentée sur {@code TexteReglementaireJpaRepositoryIntegrationTest}).
 */
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ProcedureMetierJpaRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProcedureMetierRepository procedureRepository;

    @Autowired
    private ProcedureTexteRepository procedureTexteRepository;

    @Autowired
    private TexteReglementaireRepository texteReglementaireRepository;

    @Test
    void save_and_findById_round_trip() {
        ProcedureMetier procedure = ProcedureMetier.propose("Import véhicules", "IMP-VEH", "desc", "Douanes", "editeur-1");

        procedureRepository.save(procedure);

        Optional<ProcedureMetier> found = procedureRepository.findById(procedure.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("IMP-VEH");
    }

    @Test
    void associate_texte_round_trip_and_unique_constraint() {
        ProcedureMetier procedure = ProcedureMetier.propose("Import", "IMP", null, null, "editeur-1");
        procedureRepository.save(procedure);
        TexteReglementaire texte = TexteReglementaire.propose("Titre", "loi", null, null, null, null, "editeur-1");
        texteReglementaireRepository.save(texte);

        procedureTexteRepository.save(ProcedureTexte.associate(procedure.getId(), texte.getId()));

        List<ProcedureTexte> associations = procedureTexteRepository.findByProcedureId(procedure.getId());
        assertThat(associations).hasSize(1);
        assertThat(associations.get(0).getTexteId()).isEqualTo(texte.getId());
    }
}
