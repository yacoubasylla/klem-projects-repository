package com.klem.coreapi.workflow.api.response;

import com.klem.coreapi.authorization.domain.model.RoleCode;
import com.klem.coreapi.identity.domain.model.MembershipStatus;
import com.klem.coreapi.workflow.application.service.WorkflowService.TenantOnboardingResult;

import java.util.UUID;

public record TenantOnboardingResponse(
        UUID tenantId,
        String tenantName,
        UUID userId,
        String adminEmail,
        MembershipStatus membershipStatus,
        RoleCode roleCode
) {

    public static TenantOnboardingResponse from(TenantOnboardingResult result) {
        return new TenantOnboardingResponse(
                result.tenant().getId(),
                result.tenant().getName(),
                result.membership().getUserId(),
                result.adminEmail(),
                result.membership().getStatus(),
                result.roleAssignment().getRoleCode()
        );
    }
}
