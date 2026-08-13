package com.klem.billing.api.request;

import com.klem.billing.domain.model.PaymentAggregator;
import com.klem.billing.domain.model.PaymentOperator;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Corps de POST /transactions — spécifications_techniques.md §6. `aggregator` nul signifie API
 * opérateur directe ; renseigné, il sélectionne l'agrégateur (§7 : impact PCI-DSS direct de ce choix).
 */
public record InitiateTransactionRequest(
        @NotBlank String tenantId,
        @NotNull PaymentOperator operator,
        PaymentAggregator aggregator,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount,
        @NotBlank String currency,
        String payerReference
) {
}
