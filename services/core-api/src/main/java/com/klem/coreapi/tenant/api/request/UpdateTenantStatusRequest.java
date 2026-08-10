package com.klem.coreapi.tenant.api.request;

import com.klem.coreapi.tenant.domain.model.TenantStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTenantStatusRequest(
        @NotNull(message = "must not be null")
        TenantStatus status
) {
}
