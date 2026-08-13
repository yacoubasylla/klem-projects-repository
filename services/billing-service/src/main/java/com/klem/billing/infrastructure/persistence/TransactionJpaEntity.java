package com.klem.billing.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class TransactionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private com.klem.billing.domain.model.PaymentOperator operator;

    @Enumerated(EnumType.STRING)
    private com.klem.billing.domain.model.PaymentAggregator aggregator;

    @Enumerated(EnumType.STRING)
    @Column(name = "integration_mode", nullable = false)
    private com.klem.billing.domain.model.IntegrationMode integrationMode;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(name = "payer_reference")
    private String payerReference;

    @Column(name = "operator_tx_id")
    private String operatorTxId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private com.klem.billing.domain.model.TransactionStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected TransactionJpaEntity() {
        // JPA
    }

    public TransactionJpaEntity(Long id, UUID uuid, String tenantId, String idempotencyKey,
                                 com.klem.billing.domain.model.PaymentOperator operator,
                                 com.klem.billing.domain.model.PaymentAggregator aggregator,
                                 com.klem.billing.domain.model.IntegrationMode integrationMode,
                                 BigDecimal amount, String currency, String payerReference,
                                 String operatorTxId, com.klem.billing.domain.model.TransactionStatus status,
                                 Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.uuid = uuid;
        this.tenantId = tenantId;
        this.idempotencyKey = idempotencyKey;
        this.operator = operator;
        this.aggregator = aggregator;
        this.integrationMode = integrationMode;
        this.amount = amount;
        this.currency = currency;
        this.payerReference = payerReference;
        this.operatorTxId = operatorTxId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public UUID getUuid() { return uuid; }
    public String getTenantId() { return tenantId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public com.klem.billing.domain.model.PaymentOperator getOperator() { return operator; }
    public com.klem.billing.domain.model.PaymentAggregator getAggregator() { return aggregator; }
    public com.klem.billing.domain.model.IntegrationMode getIntegrationMode() { return integrationMode; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getPayerReference() { return payerReference; }
    public String getOperatorTxId() { return operatorTxId; }
    public com.klem.billing.domain.model.TransactionStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
