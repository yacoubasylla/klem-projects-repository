package com.klem.billing.application.service;

import com.klem.billing.application.port.PaymentInitiationResult;
import com.klem.billing.application.port.PaymentProvider;
import com.klem.billing.application.port.TransactionRepository;
import com.klem.billing.application.port.WebhookCallback;
import com.klem.billing.application.port.WebhookParseResult;
import com.klem.billing.domain.model.IntegrationMode;
import com.klem.billing.domain.model.PaymentAggregator;
import com.klem.billing.domain.model.PaymentOperator;
import com.klem.billing.domain.model.Transaction;
import com.klem.billing.domain.model.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PaymentProviderRegistry providerRegistry;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(transactionRepository, providerRegistry);
    }

    @Test
    void initiatingTwiceWithSameIdempotencyKeyDoesNotCallProviderTwice() {
        Transaction existing = Transaction.initiate("tenant-1", "idem-1", PaymentOperator.ORANGE_MONEY,
                PaymentAggregator.CINETPAY, IntegrationMode.AGGREGATOR, BigDecimal.TEN, "XOF", "+2250700000000");

        when(transactionRepository.findByIdempotencyKey("tenant-1", "idem-1")).thenReturn(Optional.of(existing));

        Transaction result = transactionService.initiateViaAggregator("tenant-1", "idem-1",
                PaymentOperator.ORANGE_MONEY, PaymentAggregator.CINETPAY, BigDecimal.TEN, "XOF", "+2250700000000");

        assertThat(result).isSameAs(existing);
        verifyNoMoreInteractions(providerRegistry);
    }

    @Test
    void newInitiationCallsResolvedProviderAndPersistsPendingTransaction() {
        PaymentProvider cinetPay = mock(PaymentProvider.class);
        when(transactionRepository.findByIdempotencyKey("tenant-1", "idem-2")).thenReturn(Optional.empty());
        when(providerRegistry.resolveAggregator(PaymentAggregator.CINETPAY)).thenReturn(cinetPay);
        when(cinetPay.initiate(any())).thenReturn(new PaymentInitiationResult(null, "https://pay.cinetpay.com/xyz"));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.initiateViaAggregator("tenant-1", "idem-2",
                PaymentOperator.ORANGE_MONEY, PaymentAggregator.CINETPAY, BigDecimal.TEN, "XOF", "+2250700000000");

        assertThat(result.status()).isEqualTo(TransactionStatus.PENDING);
        verify(cinetPay).initiate(any());
    }

    @Test
    void confirmingWebhookTransitionsPendingTransactionToConfirmed() {
        Transaction pending = Transaction.initiate("tenant-1", "idem-3", PaymentOperator.ORANGE_MONEY,
                PaymentAggregator.CINETPAY, IntegrationMode.AGGREGATOR, BigDecimal.TEN, "XOF", "+2250700000000");

        PaymentProvider cinetPay = mock(PaymentProvider.class);
        WebhookCallback callback = new WebhookCallback("{}", Map.of("x-token", "sig"));

        when(providerRegistry.resolveAggregator(PaymentAggregator.CINETPAY)).thenReturn(cinetPay);
        when(cinetPay.parseCallback(callback))
                .thenReturn(new WebhookParseResult("idem-3", "op-tx-1", TransactionStatus.CONFIRMED));
        when(transactionRepository.findByIdempotencyKeyGlobal("idem-3")).thenReturn(Optional.of(pending));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.handleWebhook(null, PaymentAggregator.CINETPAY, callback);

        verify(cinetPay).verifySignature(callback);
        assertThat(result.status()).isEqualTo(TransactionStatus.CONFIRMED);
        assertThat(result.operatorTxId()).isEqualTo("op-tx-1");
    }

    @Test
    void webhookOnAlreadyConfirmedTransactionIsIdempotentAndDoesNotRewriteState() {
        Transaction confirmed = Transaction.initiate("tenant-1", "idem-4", PaymentOperator.ORANGE_MONEY,
                PaymentAggregator.CINETPAY, IntegrationMode.AGGREGATOR, BigDecimal.TEN, "XOF", "+2250700000000");
        confirmed.confirm("op-tx-2");

        PaymentProvider cinetPay = mock(PaymentProvider.class);
        WebhookCallback callback = new WebhookCallback("{}", Map.of("x-token", "sig"));

        when(providerRegistry.resolveAggregator(PaymentAggregator.CINETPAY)).thenReturn(cinetPay);
        when(cinetPay.parseCallback(callback))
                .thenReturn(new WebhookParseResult("idem-4", "op-tx-2", TransactionStatus.CONFIRMED));
        when(transactionRepository.findByIdempotencyKeyGlobal("idem-4")).thenReturn(Optional.of(confirmed));

        Transaction result = transactionService.handleWebhook(null, PaymentAggregator.CINETPAY, callback);

        assertThat(result.status()).isEqualTo(TransactionStatus.CONFIRMED);
        verify(transactionRepository, org.mockito.Mockito.never()).save(any());
    }
}
