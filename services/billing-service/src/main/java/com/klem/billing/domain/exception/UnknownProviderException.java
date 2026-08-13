package com.klem.billing.domain.exception;

public class UnknownProviderException extends RuntimeException {

    public UnknownProviderException(String operator) {
        super("Aucun PaymentProvider enregistré pour l'opérateur/agrégateur : " + operator);
    }
}
