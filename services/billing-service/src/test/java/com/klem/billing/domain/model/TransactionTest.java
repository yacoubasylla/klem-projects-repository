package com.klem.billing.domain.model;

import com.klem.billing.domain.exception.InvalidTransactionStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionTest {

    private Transaction newPendingTransaction() {
        return Transaction.initiate("tenant-1", "idem-key-1", PaymentOperator.ORANGE_MONEY,
                PaymentAggregator.CINETPAY, IntegrationMode.AGGREGATOR, BigDecimal.TEN, "XOF", "+2250700000000");
    }

    @Test
    void confirmMovesFromPendingToConfirmed() {
        Transaction transaction = newPendingTransaction();
        transaction.confirm("operator-tx-123");

        assertThat(transaction.status()).isEqualTo(TransactionStatus.CONFIRMED);
        assertThat(transaction.operatorTxId()).isEqualTo("operator-tx-123");
    }

    @Test
    void failMovesFromPendingToFailed() {
        Transaction transaction = newPendingTransaction();
        transaction.fail();

        assertThat(transaction.status()).isEqualTo(TransactionStatus.FAILED);
    }

    @Test
    void confirmedTransactionCannotTransitionDirectlyToFailed() {
        Transaction transaction = newPendingTransaction();
        transaction.confirm("operator-tx-123");

        assertThatThrownBy(transaction::fail).isInstanceOf(InvalidTransactionStateException.class);
    }

    @Test
    void refundOnlyReachableThroughRefundInitiated() {
        Transaction transaction = newPendingTransaction();
        transaction.confirm("operator-tx-123");

        assertThatThrownBy(transaction::refund).isInstanceOf(InvalidTransactionStateException.class);

        transaction.initiateRefund();
        transaction.refund();

        assertThat(transaction.status()).isEqualTo(TransactionStatus.REFUNDED);
    }

    @Test
    void failedTransactionIsTerminalForCallback() {
        Transaction transaction = newPendingTransaction();
        transaction.fail();

        assertThat(transaction.isTerminalForCallback()).isTrue();
    }

    @Test
    void directApiTransactionCannotCarryAnAggregator() {
        assertThatThrownBy(() -> Transaction.initiate("tenant-1", "idem-key-2", PaymentOperator.WAVE,
                PaymentAggregator.CINETPAY, IntegrationMode.DIRECT_API, BigDecimal.ONE, "XOF", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
