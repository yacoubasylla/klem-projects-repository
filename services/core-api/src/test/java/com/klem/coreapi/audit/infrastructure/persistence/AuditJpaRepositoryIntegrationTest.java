package com.klem.coreapi.audit.infrastructure.persistence;

import com.klem.coreapi.audit.application.port.AuditEntryRepository;
import com.klem.coreapi.audit.domain.model.AuditEntry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AuditJpaRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private AuditEntryRepository auditEntryRepository;

    @Test
    void save_and_query_by_tenant_paginated() {
        UUID tenantId = UUID.randomUUID();
        auditEntryRepository.save(AuditEntry.capture(UUID.randomUUID(), "tenant.created", tenantId, tenantId, Instant.now(), "{}"));
        auditEntryRepository.save(AuditEntry.capture(UUID.randomUUID(), "tenant.status.changed", tenantId, tenantId, Instant.now(), "{}"));
        auditEntryRepository.save(AuditEntry.capture(UUID.randomUUID(), "user.activated", null, UUID.randomUUID(), Instant.now(), "{}"));

        Page<AuditEntry> page = auditEntryRepository.findByTenantId(tenantId, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting(AuditEntry::getEventType)
                .containsExactlyInAnyOrder("tenant.created", "tenant.status.changed");
    }

    @Test
    void unrelated_tenant_returns_empty_page() {
        Page<AuditEntry> page = auditEntryRepository.findByTenantId(UUID.randomUUID(), PageRequest.of(0, 10));
        assertThat(page.getContent()).isEmpty();
    }
}
