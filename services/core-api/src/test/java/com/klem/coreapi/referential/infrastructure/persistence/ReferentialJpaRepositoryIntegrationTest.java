package com.klem.coreapi.referential.infrastructure.persistence;

import com.klem.coreapi.referential.application.port.ReferentialEntryRepository;
import com.klem.coreapi.referential.application.port.ReferentialListRepository;
import com.klem.coreapi.referential.domain.model.ReferentialEntry;
import com.klem.coreapi.referential.domain.model.ReferentialList;
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
 * Vérifie contre un PostgreSQL réel que la migration {@code V3__create_referential_tables.sql}
 * (seed inclus) s'applique correctement — pas seulement le mapping JPA.
 */
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ReferentialJpaRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ReferentialListRepository listRepository;

    @Autowired
    private ReferentialEntryRepository entryRepository;

    @Test
    void seeded_countries_list_is_present_with_corridor_countries() {
        Optional<ReferentialList> countries = listRepository.findByCode("countries");
        assertThat(countries).isPresent();

        List<ReferentialEntry> entries = entryRepository.findByListIdOrderBySortOrderAsc(countries.get().getId());
        assertThat(entries).extracting(ReferentialEntry::getEntryCode)
                .containsExactly("CI", "ML", "BF");
    }

    @Test
    void seeded_currencies_list_contains_xof() {
        Optional<ReferentialList> currencies = listRepository.findByCode("currencies");
        assertThat(currencies).isPresent();

        List<ReferentialEntry> entries = entryRepository.findByListIdOrderBySortOrderAsc(currencies.get().getId());
        assertThat(entries).extracting(ReferentialEntry::getEntryCode).containsExactly("XOF");
    }

    @Test
    void unknown_list_code_returns_empty() {
        assertThat(listRepository.findByCode("inconnu")).isEmpty();
    }
}
