package com.klem.billing.api.response;

import com.klem.billing.domain.model.IntegrationMode;
import com.klem.billing.domain.model.PaymentAggregator;
import com.klem.billing.domain.model.PaymentOperator;
import com.klem.billing.domain.model.Transaction;
import com.klem.billing.domain.model.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String tenantId,
        PaymentOperator operator,
        PaymentAggregator aggregator,
        IntegrationMode integrationMode,
        BigDecimal amount,
        String currency,
        String operatorTxId,
        TransactionStatus status,
        Instant createdAt,
        Instant updatedAt
) {

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(transaction.id(), transaction.tenantId(), transaction.operator(),
                transaction.aggregator(), transaction.integrationMode(), transaction.amount(),
                transaction.currency(), transaction.operatorTxId(), transaction.status(),
                transaction.createdAt(), transaction.updatedAt());
    }
}
