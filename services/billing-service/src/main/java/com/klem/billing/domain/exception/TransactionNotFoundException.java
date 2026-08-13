package com.klem.billing.domain.exception;

public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(String reference) {
        super("Transaction introuvable : " + reference);
    }
}
