package com.klem.coreapi.audit.infrastructure.persistence;

import com.klem.coreapi.audit.application.port.AuditEntryRepository;
import com.klem.coreapi.audit.domain.model.AuditEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface AuditEntryJpaRepository extends JpaRepository<AuditEntry, UUID>, AuditEntryRepository {

    @Override
    Page<AuditEntry> findByTenantId(UUID tenantId, Pageable pageable);
}
