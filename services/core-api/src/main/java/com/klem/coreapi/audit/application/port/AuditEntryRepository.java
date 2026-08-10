package com.klem.coreapi.audit.application.port;

import com.klem.coreapi.audit.domain.model.AuditEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AuditEntryRepository {

    AuditEntry save(AuditEntry entry);

    Page<AuditEntry> findByTenantId(UUID tenantId, Pageable pageable);
}
