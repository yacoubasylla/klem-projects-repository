package com.klem.cantine.paiement.service;

import com.klem.cantine.paiement.strategy.PaymentStrategy;
import com.klem.cantine.paiement.strategy.enums.PaymentProviderType;
import com.klem.cantine.paiement.strategy.exception.PaymentProviderException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Sélectionne la {@link PaymentStrategy} à utiliser pour une demande de paiement.
 * <p>
 * Spring injecte automatiquement tous les beans {@link PaymentStrategy} déclarés dans le
 * contexte (un seul par {@link PaymentProviderType} — ajouter un fournisseur ne demande donc
 * aucune modification de cette classe, voir {@code strategy.impl.package-info}).
 */
@Component
@Slf4j
public class PaymentStrategyFactory {

    private final List<PaymentStrategy> strategies;
    private final String defaultProviderCode;

    public PaymentStrategyFactory(List<PaymentStrategy> strategies,
                                   org.springframework.core.env.Environment environment) {
        this.strategies = strategies;
        this.defaultProviderCode = environment.getProperty("klem.payment.default-provider", "CINETPAY");
    }

    private Map<PaymentProviderType, PaymentStrategy> strategiesByType;

    @PostConstruct
    void indexStrategies() {
        strategiesByType = strategies.stream()
                .collect(Collectors.toMap(
                        PaymentStrategy::getProviderType,
                        Function.identity(),
                        (a, b) -> a,
                        () -> new EnumMap<>(PaymentProviderType.class)));
        log.info("PaymentStrategyFactory initialisée avec les providers : {} (défaut : {})",
                strategiesByType.keySet(), defaultProviderCode);
    }

    /**
     * Retourne la stratégie du provider demandé, ou celle configurée par
     * {@code klem.payment.default-provider} si {@code provider} est {@code null}.
     */
    public PaymentStrategy getStrategy(PaymentProviderType provider) {
        PaymentProviderType resolved = provider != null ? provider : defaultProvider();
        PaymentStrategy strategy = strategiesByType.get(resolved);
        if (strategy == null) {
            throw new PaymentProviderException(
                    "Aucune stratégie de paiement disponible pour le provider : " + resolved);
        }
        return strategy;
    }

    private PaymentProviderType defaultProvider() {
        try {
            return PaymentProviderType.valueOf(defaultProviderCode);
        } catch (IllegalArgumentException e) {
            throw new PaymentProviderException(
                    "Provider de paiement par défaut invalide (klem.payment.default-provider) : "
                            + defaultProviderCode);
        }
    }
}
