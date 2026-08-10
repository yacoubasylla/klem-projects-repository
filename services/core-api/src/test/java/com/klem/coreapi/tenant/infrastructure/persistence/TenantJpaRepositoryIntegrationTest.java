package com.klem.coreapi.tenant.infrastructure.persistence;

import com.klem.coreapi.tenant.application.port.TenantRepository;
import com.klem.coreapi.tenant.domain.model.Tenant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Vérifie la migration Flyway {@code V1__create_tenant_table.sql} et le mapping JPA réel, contre
 * un PostgreSQL réel (Testcontainers) — pas H2, pour rester fidèle au moteur de production
 * ({@code KLEM_MASTER_SYSTEM_DIRECTIVE.md} §18).
 */
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TenantJpaRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void save_and_findById_round_trip() {
        Tenant tenant = Tenant.create("Cantine-Connect pilote", "restauration scolaire");

        tenantRepository.save(tenant);

        Optional<Tenant> found = tenantRepository.findById(tenant.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Cantine-Connect pilote");
        assertThat(found.get().getSector()).isEqualTo("restauration scolaire");
    }

    @Test
    void findById_returns_empty_when_absent() {
        assertThat(tenantRepository.findById(UUID.randomUUID())).isEmpty();
    }
}
