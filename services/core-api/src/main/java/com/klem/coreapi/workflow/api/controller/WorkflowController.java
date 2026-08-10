package com.klem.coreapi.workflow.api.controller;

import com.klem.coreapi.workflow.api.request.OnboardTenantRequest;
import com.klem.coreapi.workflow.api.response.TenantOnboardingResponse;
import com.klem.coreapi.workflow.application.service.WorkflowService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Réservé à {@code PLATFORM_ADMIN} — équivalent à {@code POST /api/v1/tenants} (même restriction),
 * dont ce workflow ne fait qu'enchaîner l'appel avec deux autres.
 */
@RestController
@RequestMapping("/api/v1/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/tenant-onboarding")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public TenantOnboardingResponse onboardTenant(@Valid @RequestBody OnboardTenantRequest request) {
        var result = workflowService.onboardTenant(
                request.tenantName(), request.tenantSector(), request.adminEmail(), request.adminDisplayName());
        return TenantOnboardingResponse.from(result);
    }
}
