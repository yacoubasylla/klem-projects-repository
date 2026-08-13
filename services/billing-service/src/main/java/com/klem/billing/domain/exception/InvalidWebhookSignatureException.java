package com.klem.billing.domain.exception;

/** Levée lorsqu'un callback opérateur/agrégateur échoue la vérification de signature — spécifications_fonctionnelles.md §4.2. */
public class InvalidWebhookSignatureException extends RuntimeException {

    public InvalidWebhookSignatureException(String operatorOrAggregator) {
        super("Signature de callback invalide pour " + operatorOrAggregator);
    }
}
