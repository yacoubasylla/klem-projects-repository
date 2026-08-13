package com.klem.billing.infrastructure.persistence;

import com.klem.billing.application.port.TransactionRepository;
import com.klem.billing.domain.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final TransactionSpringDataRepository springDataRepository;

    public TransactionRepositoryAdapter(TransactionSpringDataRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionJpaEntity existing = springDataRepository.findByUuid(transaction.id()).orElse(null);
        Long id = existing != null ? existing.getId() : null;

        TransactionJpaEntity entity = new TransactionJpaEntity(id, transaction.id(), transaction.tenantId(),
                transaction.idempotencyKey(), transaction.operator(), transaction.aggregator(),
                transaction.integrationMode(), transaction.amount(), transaction.currency(),
                transaction.payerReference(), transaction.operatorTxId(), transaction.status(),
                transaction.createdAt(), transaction.updatedAt());

        return toDomain(springDataRepository.save(entity));
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return springDataRepository.findByUuid(id).map(this::toDomain);
    }

    @Override
    public Optional<Transaction> findByIdempotencyKey(String tenantId, String idempotencyKey) {
        return springDataRepository.findByTenantIdAndIdempotencyKey(tenantId, idempotencyKey).map(this::toDomain);
    }

    @Override
    public Optional<Transaction> findByIdempotencyKeyGlobal(String idempotencyKey) {
        return springDataRepository.findByIdempotencyKey(idempotencyKey).map(this::toDomain);
    }

    @Override
    public Optional<Transaction> findByOperatorTxId(String operatorTxId) {
        return springDataRepository.findByOperatorTxId(operatorTxId).map(this::toDomain);
    }

    private Transaction toDomain(TransactionJpaEntity entity) {
        return Transaction.reconstitute(entity.getUuid(), entity.getTenantId(), entity.getIdempotencyKey(),
                entity.getOperator(), entity.getAggregator(), entity.getIntegrationMode(), entity.getAmount(),
                entity.getCurrency(), entity.getPayerReference(), entity.getOperatorTxId(), entity.getStatus(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
