package com.klem.billing.application.port;

import java.math.BigDecimal;

public record PaymentInitiationCommand(
        String tenantId,
        String idempotencyKey,
        BigDecimal amount,
        String currency,
        String payerReference
) {
}
