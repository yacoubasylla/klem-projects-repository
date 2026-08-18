package com.klem.cantine.paiement.strategy.exception;

/**
 * Erreur métier levée par une {@link com.klem.cantine.paiement.strategy.PaymentStrategy}
 * (fournisseur indisponible, réponse invalide, signature de webhook rejetée...).
 * <p>
 * Hérite volontairement de {@link IllegalStateException} pour être interceptée sans
 * modification par le {@code GlobalExceptionHandler} existant (réponse HTTP 409),
 * au même titre que les erreurs déjà levées par {@code CinetPayProvider}/{@code PayDunyaProvider}.
 */
public class PaymentProviderException extends IllegalStateException {

    public PaymentProviderException(String message) {
        super(message);
    }

    public PaymentProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
