package com.klem.billing.application.service;

import com.klem.billing.application.port.PaymentInitiationCommand;
import com.klem.billing.application.port.PaymentInitiationResult;
import com.klem.billing.application.port.PaymentProvider;
import com.klem.billing.application.port.RefundCommand;
import com.klem.billing.application.port.TransactionRepository;
import com.klem.billing.application.port.WebhookCallback;
import com.klem.billing.application.port.WebhookParseResult;
import com.klem.billing.domain.exception.TransactionNotFoundException;
import com.klem.billing.domain.model.IntegrationMode;
import com.klem.billing.domain.model.PaymentAggregator;
import com.klem.billing.domain.model.PaymentOperator;
import com.klem.billing.domain.model.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Cœur du module, indépendant de l'opérateur/agrégateur concret — spécifications_techniques.md §5/§6.
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PaymentProviderRegistry providerRegistry;

    public TransactionService(TransactionRepository transactionRepository, PaymentProviderRegistry providerRegistry) {
        this.transactionRepository = transactionRepository;
        this.providerRegistry = providerRegistry;
    }

    @Transactional
    public Transaction initiateViaAggregator(String tenantId, String idempotencyKey, PaymentOperator operator,
                                              PaymentAggregator aggregator, BigDecimal amount, String currency,
                                              String payerReference) {
        return transactionRepository.findByIdempotencyKey(tenantId, idempotencyKey)
                .orElseGet(() -> {
                    PaymentProvider provider = providerRegistry.resolveAggregator(aggregator);
                    Transaction transaction = Transaction.initiate(tenantId, idempotencyKey, operator, aggregator,
                            IntegrationMode.AGGREGATOR, amount, currency, payerReference);
                    return persistAfterInitiation(transaction, provider);
                });
    }

    @Transactional
    public Transaction initiateViaDirectApi(String tenantId, String idempotencyKey, PaymentOperator operator,
                                             BigDecimal amount, String currency, String payerReference) {
        return transactionRepository.findByIdempotencyKey(tenantId, idempotencyKey)
                .orElseGet(() -> {
                    PaymentProvider provider = providerRegistry.resolveDirect(operator);
                    Transaction transaction = Transaction.initiate(tenantId, idempotencyKey, operator, null,
                            IntegrationMode.DIRECT_API, amount, currency, payerReference);
                    return persistAfterInitiation(transaction, provider);
                });
    }

    private Transaction persistAfterInitiation(Transaction transaction, PaymentProvider provider) {
        PaymentInitiationCommand command = new PaymentInitiationCommand(transaction.tenantId(),
                transaction.idempotencyKey(), transaction.amount(), transaction.currency(), transaction.payerReference());
        PaymentInitiationResult result = provider.initiate(command);
        transaction.attachProvisionalOperatorTxId(result.operatorTxId());
        return transactionRepository.save(transaction);
    }

    /**
     * Réception d'un callback opérateur/agrégateur. Idempotent : un callback sur une transaction déjà
     * CONFIRMED/FAILED ne rejoue aucune transition (spécifications_fonctionnelles.md §4.2).
     */
    @Transactional
    public Transaction handleWebhook(PaymentOperator operator, PaymentAggregator aggregator, WebhookCallback callback) {
        PaymentProvider provider = aggregator != null
                ? providerRegistry.resolveAggregator(aggregator)
                : providerRegistry.resolveDirect(operator);

        provider.verifySignature(callback);
        WebhookParseResult parsed = provider.parseCallback(callback);

        Transaction transaction = transactionRepository.findByIdempotencyKeyGlobal(parsed.idempotencyKey())
                .or(() -> transactionRepository.findByOperatorTxId(parsed.operatorTxId()))
                .orElseThrow(() -> new TransactionNotFoundException(parsed.idempotencyKey()));

        if (transaction.isTerminalForCallback()) {
            return transaction;
        }

        switch (parsed.newStatus()) {
            case CONFIRMED -> transaction.confirm(parsed.operatorTxId());
            case FAILED -> transaction.fail();
            default -> throw new IllegalStateException("État de callback inattendu : " + parsed.newStatus());
        }
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction refund(UUID transactionId, String reason) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId.toString()));

        PaymentProvider provider = transaction.integrationMode() == IntegrationMode.AGGREGATOR
                ? providerRegistry.resolveAggregator(transaction.aggregator())
                : providerRegistry.resolveDirect(transaction.operator());

        transaction.initiateRefund();
        transactionRepository.save(transaction);

        provider.initiateRefund(new RefundCommand(transaction.operatorTxId(), transaction.amount(),
                transaction.currency(), reason));

        transaction.refund();
        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public Transaction findById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id.toString()));
    }
}
