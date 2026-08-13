package com.klem.billing.application.port;

/**
 * @param operatorTxId  identifiant côté opérateur/agrégateur, si déjà connu à l'initiation
 * @param redirectUrl   page de paiement hébergée à rediriger le payeur vers (mode AGGREGATOR
 *                      typique) ; nul en mode DIRECT_API/USSD
 */
public record PaymentInitiationResult(String operatorTxId, String redirectUrl) {
}
