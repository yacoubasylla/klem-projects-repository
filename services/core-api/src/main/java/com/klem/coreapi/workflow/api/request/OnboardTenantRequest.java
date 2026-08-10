package com.klem.coreapi.workflow.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OnboardTenantRequest(

        @NotBlank(message = "must not be blank")
        @Size(max = 200, message = "must be at most 200 characters")
        String tenantName,

        @Size(max = 100, message = "must be at most 100 characters")
        String tenantSector,

        @NotBlank(message = "must not be blank")
        @Email(message = "must be a valid email address")
        String adminEmail,

        @Size(max = 200, message = "must be at most 200 characters")
        String adminDisplayName
) {
}
