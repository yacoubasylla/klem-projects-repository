package com.klem.billing.application.port;

import com.klem.billing.domain.model.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID id);

    Optional<Transaction> findByIdempotencyKey(String tenantId, String idempotencyKey);

    /** Résolution côté callback, où le tenant n'est pas connu — la clé d'idempotence, générée
     *  côté client, est unique globalement (contrainte d'unicité en base, voir V1__init_billing_schema.sql). */
    Optional<Transaction> findByIdempotencyKeyGlobal(String idempotencyKey);

    Optional<Transaction> findByOperatorTxId(String operatorTxId);
}
