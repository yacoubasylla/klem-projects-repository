package com.klem.billing.domain.exception;

import com.klem.billing.domain.model.TransactionStatus;

public class InvalidTransactionStateException extends RuntimeException {

    public InvalidTransactionStateException(TransactionStatus current, TransactionStatus target) {
        super("Transition interdite : %s → %s (voir specifications_techniques.md §5)"
                .formatted(current, target));
    }
}
