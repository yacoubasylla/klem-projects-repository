package com.klem.billing.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface TransactionSpringDataRepository extends JpaRepository<TransactionJpaEntity, Long> {

    Optional<TransactionJpaEntity> findByUuid(UUID uuid);

    Optional<TransactionJpaEntity> findByTenantIdAndIdempotencyKey(String tenantId, String idempotencyKey);

    Optional<TransactionJpaEntity> findByIdempotencyKey(String idempotencyKey);

    Optional<TransactionJpaEntity> findByOperatorTxId(String operatorTxId);
}
