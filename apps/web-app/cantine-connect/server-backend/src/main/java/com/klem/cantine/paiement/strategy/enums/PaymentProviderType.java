package com.klem.cantine.paiement.strategy.enums;

/**
 * Fournisseur de paiement pris en charge par le contrat unifié {@link com.klem.cantine.paiement.strategy.PaymentStrategy}.
 * <p>
 * Distinct des codes historiques (String) de {@link com.klem.cantine.paiement.provider.PaymentProvider#getCode()}
 * conservés tels quels pour la rétrocompatibilité (`PAIEMENT_PROVIDER_ACTIF` en base) — {@link #CINETPAY}
 * correspond au code historique {@code "CINETPAY"}, seul point de recouvrement volontaire entre les deux
 * contrats le temps de la migration progressive.
 */
public enum PaymentProviderType {
    CINETPAY,
    ORANGE_MONEY_CI,
    MTN_MOMO_CI,
    WAVE_CI
}
