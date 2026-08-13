package com.klem.billing.domain.model;

import com.klem.billing.domain.exception.InvalidTransactionStateException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Agrégat racine — transaction de paiement, indépendante du mode d'intégration (API directe ou
 * agrégateur). Voir specifications_techniques.md §5 pour la machine à états qu'elle applique.
 */
public class Transaction {

    private final UUID id;
    private final String tenantId;
    private final String idempotencyKey;
    private final PaymentOperator operator;
    private final PaymentAggregator aggregator;
    private final IntegrationMode integrationMode;
    private final BigDecimal amount;
    private final String currency;
    private final String payerReference;
    private String operatorTxId;
    private TransactionStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Transaction(UUID id, String tenantId, String idempotencyKey, PaymentOperator operator,
                         PaymentAggregator aggregator, IntegrationMode integrationMode, BigDecimal amount,
                         String currency, String payerReference, String operatorTxId,
                         TransactionStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.idempotencyKey = Objects.requireNonNull(idempotencyKey, "idempotencyKey");
        this.operator = Objects.requireNonNull(operator, "operator");
        this.aggregator = aggregator;
        this.integrationMode = Objects.requireNonNull(integrationMode, "integrationMode");
        this.amount = Objects.requireNonNull(amount, "amount");
        this.currency = Objects.requireNonNull(currency, "currency");
        this.payerReference = payerReference;
        this.operatorTxId = operatorTxId;
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = updatedAt;
    }

    public static Transaction initiate(String tenantId, String idempotencyKey, PaymentOperator operator,
                                        PaymentAggregator aggregator, IntegrationMode integrationMode,
                                        BigDecimal amount, String currency, String payerReference) {
        if (integrationMode == IntegrationMode.AGGREGATOR && aggregator == null) {
            throw new IllegalArgumentException("aggregator requis en mode AGGREGATOR");
        }
        if (integrationMode == IntegrationMode.DIRECT_API && aggregator != null) {
            throw new IllegalArgumentException("aggregator doit être nul en mode DIRECT_API");
        }
        Instant now = Instant.now();
        return new Transaction(UUID.randomUUID(), tenantId, idempotencyKey, operator, aggregator,
                integrationMode, amount, currency, payerReference, null, TransactionStatus.PENDING,
                now, now);
    }

    public static Transaction reconstitute(UUID id, String tenantId, String idempotencyKey,
                                            PaymentOperator operator, PaymentAggregator aggregator,
                                            IntegrationMode integrationMode, BigDecimal amount,
                                            String currency, String payerReference, String operatorTxId,
                                            TransactionStatus status, Instant createdAt, Instant updatedAt) {
        return new Transaction(id, tenantId, idempotencyKey, operator, aggregator, integrationMode,
                amount, currency, payerReference, operatorTxId, status, createdAt, updatedAt);
    }

    private void transitionTo(TransactionStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new InvalidTransactionStateException(status, target);
        }
        this.status = target;
        this.updatedAt = Instant.now();
    }

    public void confirm(String operatorTxId) {
        transitionTo(TransactionStatus.CONFIRMED);
        this.operatorTxId = operatorTxId;
    }

    /**
     * Certains fournisseurs (Wave, MTN MoMo) renvoient un identifiant de transaction dès la réponse
     * synchrone d'initiation, avant toute confirmation par callback. Utile pour la réconciliation
     * précoce ; ne fait transiter aucun état — seul {@link #confirm} ou {@link #fail} le fait.
     */
    public void attachProvisionalOperatorTxId(String operatorTxId) {
        if (status == TransactionStatus.PENDING && operatorTxId != null) {
            this.operatorTxId = operatorTxId;
            this.updatedAt = Instant.now();
        }
    }

    public void fail() {
        transitionTo(TransactionStatus.FAILED);
    }

    public void initiateRefund() {
        transitionTo(TransactionStatus.REFUND_INITIATED);
    }

    public void refund() {
        transitionTo(TransactionStatus.REFUNDED);
    }

    /** Idempotence côté serveur : une re-tentative sur une transaction déjà tranchée ne rejoue rien. */
    public boolean isTerminalForCallback() {
        return status == TransactionStatus.CONFIRMED || status == TransactionStatus.FAILED;
    }

    public UUID id() { return id; }
    public String tenantId() { return tenantId; }
    public String idempotencyKey() { return idempotencyKey; }
    public PaymentOperator operator() { return operator; }
    public PaymentAggregator aggregator() { return aggregator; }
    public IntegrationMode integrationMode() { return integrationMode; }
    public BigDecimal amount() { return amount; }
    public String currency() { return currency; }
    public String payerReference() { return payerReference; }
    public String operatorTxId() { return operatorTxId; }
    public TransactionStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
