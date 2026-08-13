package com.klem.billing.application.service;

import com.klem.billing.application.port.PaymentProvider;
import com.klem.billing.domain.exception.UnknownProviderException;
import com.klem.billing.domain.model.PaymentAggregator;
import com.klem.billing.domain.model.PaymentOperator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Résolution du {@link PaymentProvider} à utiliser pour un opérateur donné, en fonction du mode
 * d'intégration choisi (API directe ou agrégateur — les deux peuvent être enregistrés pour le même
 * opérateur, ex. Orange Money joignable en direct ou via CinetPay). Chaque implémentation
 * {@code PaymentProvider} est un bean Spring auto-découvert : ajouter un opérateur/agrégateur ne
 * nécessite pas de modifier cette classe.
 */
@Component
public class PaymentProviderRegistry {

    private final Map<PaymentOperator, PaymentProvider> directProviders;
    private final Map<PaymentAggregator, PaymentProvider> aggregatorProviders;

    public PaymentProviderRegistry(List<PaymentProvider> providers) {
        this.directProviders = providers.stream()
                .filter(p -> p.aggregator() == null)
                .collect(Collectors.toMap(PaymentProvider::operator, Function.identity()));
        this.aggregatorProviders = providers.stream()
                .filter(p -> p.aggregator() != null)
                .collect(Collectors.toMap(PaymentProvider::aggregator, Function.identity()));
    }

    public PaymentProvider resolveDirect(PaymentOperator operator) {
        PaymentProvider provider = directProviders.get(operator);
        if (provider == null) {
            throw new UnknownProviderException(operator.name());
        }
        return provider;
    }

    public PaymentProvider resolveAggregator(PaymentAggregator aggregator) {
        PaymentProvider provider = aggregatorProviders.get(aggregator);
        if (provider == null) {
            throw new UnknownProviderException(aggregator.name());
        }
        return provider;
    }
}
