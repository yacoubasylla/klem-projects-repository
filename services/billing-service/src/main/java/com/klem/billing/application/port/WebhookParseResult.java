package com.klem.billing.application.port;

import com.klem.billing.domain.model.TransactionStatus;

/**
 * @param idempotencyKey référence interne (clé d'idempotence) que le callback permet de retrouver
 * @param operatorTxId   identifiant de transaction côté opérateur/agrégateur
 * @param newStatus      CONFIRMED ou FAILED uniquement — un callback ne produit jamais d'autre état
 */
public record WebhookParseResult(String idempotencyKey, String operatorTxId, TransactionStatus newStatus) {

    public WebhookParseResult {
        if (newStatus != TransactionStatus.CONFIRMED && newStatus != TransactionStatus.FAILED) {
            throw new IllegalArgumentException("Un callback ne peut produire que CONFIRMED ou FAILED, reçu : " + newStatus);
        }
    }
}
