package com.klem.referentielapi.operationcommerce.api.request;

import com.klem.referentielapi.operationcommerce.domain.model.TypeOperation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateOperationCommerceRequest(

        @NotBlank(message = "must not be blank")
        @Size(max = 300, message = "must be at most 300 characters")
        String nom,

        @NotBlank(message = "must not be blank")
        @Size(max = 50, message = "must be at most 50 characters")
        String code,

        @NotNull(message = "must not be null")
        TypeOperation type,

        @NotNull(message = "must not be null")
        UUID procedureId
) {
}
