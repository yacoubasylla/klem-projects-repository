package com.klem.coreapi.tenant.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTenantRequest(

        @NotBlank(message = "must not be blank")
        @Size(max = 200, message = "must be at most 200 characters")
        String name,

        @Size(max = 100, message = "must be at most 100 characters")
        String sector
) {
}
