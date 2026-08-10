package com.klem.coreapi.audit.api.controller;

import com.klem.coreapi.audit.api.response.AuditEntryResponse;
import com.klem.coreapi.audit.application.service.AuditService;
import com.klem.coreapi.shared.api.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Réservé à {@code PLATFORM_ADMIN} — le journal d'audit est une donnée sensible par nature
 * (`KLEM_MASTER_SYSTEM_DIRECTIVE.md` §7 : audit des opérations sensibles).
 */
@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/audit-events")
public class AuditController {

    private static final int MAX_PAGE_SIZE = 200;

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public PageResponse<AuditEntryResponse> list(@PathVariable UUID tenantId,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        int boundedSize = Math.min(size, MAX_PAGE_SIZE);
        var pageable = PageRequest.of(page, boundedSize, Sort.by(Sort.Direction.DESC, "recordedAt"));
        return PageResponse.from(auditService.getEntries(tenantId, pageable).map(AuditEntryResponse::from));
    }
}
