package com.klem.coreapi.tenant.api.controller;

import com.klem.coreapi.tenant.api.request.CreateTenantRequest;
import com.klem.coreapi.tenant.api.request.UpdateTenantStatusRequest;
import com.klem.coreapi.tenant.api.response.TenantResponse;
import com.klem.coreapi.tenant.application.service.TenantService;
import com.klem.coreapi.tenant.domain.model.Tenant;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

/**
 * Controller mince — la logique métier vit dans {@link TenantService}
 * ({@code KLEM_MASTER_SYSTEM_DIRECTIVE.md} §5).
 * <p>
 * Les trois endpoints sont restreints au rôle {@code PLATFORM_ADMIN} pour ce premier Sprint
 * (README.md §5 ne le précise explicitement que pour la création ; lecture et changement de statut
 * sont traités en administratif par défaut ici, à assouplir plus tard — ex. self-service tenant —
 * si un besoin réel apparaît, pas par anticipation). Le rôle est lu depuis la claim {@code roles}
 * du JWT, voir {@code SecurityConfig} pour le contrat exact attendu du token.
 */
@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<TenantResponse> create(@Valid @RequestBody CreateTenantRequest request) {
        Tenant tenant = tenantService.createTenant(request.name(), request.sector());
        TenantResponse body = TenantResponse.from(tenant);
        return ResponseEntity.created(URI.create("/api/v1/tenants/" + tenant.getId())).body(body);
    }

    @GetMapping("/{tenantId}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public TenantResponse get(@PathVariable UUID tenantId) {
        return TenantResponse.from(tenantService.getTenant(tenantId));
    }

    @PatchMapping("/{tenantId}/status")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public TenantResponse updateStatus(@PathVariable UUID tenantId,
                                        @Valid @RequestBody UpdateTenantStatusRequest request) {
        return TenantResponse.from(tenantService.changeStatus(tenantId, request.status()));
    }
}
