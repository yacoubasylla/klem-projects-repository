package com.klem.billing.domain.model;

/**
 * Deux familles d'intégration coexistantes, chacune un {@link com.klem.billing.application.port.PaymentProvider}
 * distinct derrière la même interface — voir spécifications_techniques.md §7 pour l'impact PCI-DSS
 * de ce choix (AGGREGATOR = SAQ A par défaut, DIRECT_API = scope PCI-DSS élargi).
 */
public enum IntegrationMode {
    DIRECT_API,
    AGGREGATOR
}
