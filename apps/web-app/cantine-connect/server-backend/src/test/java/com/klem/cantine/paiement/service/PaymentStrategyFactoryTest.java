package com.klem.cantine.paiement.service;

import com.klem.cantine.paiement.strategy.PaymentStrategy;
import com.klem.cantine.paiement.strategy.enums.PaymentProviderType;
import com.klem.cantine.paiement.strategy.exception.PaymentProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentStrategyFactoryTest {

    @Mock private PaymentStrategy cinetPayStrategy;
    @Mock private PaymentStrategy orangeMoneyStrategy;
    @Mock private Environment environment;

    private PaymentStrategyFactory factory;

    @BeforeEach
    void setUp() {
        lenient().when(cinetPayStrategy.getProviderType()).thenReturn(PaymentProviderType.CINETPAY);
        lenient().when(orangeMoneyStrategy.getProviderType()).thenReturn(PaymentProviderType.ORANGE_MONEY_CI);
    }

    private PaymentStrategyFactory buildFactory(String defaultProvider) {
        when(environment.getProperty("klem.payment.default-provider", "CINETPAY")).thenReturn(defaultProvider);
        PaymentStrategyFactory f = new PaymentStrategyFactory(
                List.of(cinetPayStrategy, orangeMoneyStrategy), environment);
        f.indexStrategies();
        return f;
    }

    @Test
    void getStrategy_avecProviderExplicite_retourneLaStrategieCorrespondante() {
        factory = buildFactory("CINETPAY");

        assertThat(factory.getStrategy(PaymentProviderType.ORANGE_MONEY_CI)).isSameAs(orangeMoneyStrategy);
    }

    @Test
    void getStrategy_sansProvider_retombeSurLeProviderParDefaut() {
        factory = buildFactory("ORANGE_MONEY_CI");

        assertThat(factory.getStrategy(null)).isSameAs(orangeMoneyStrategy);
    }

    @Test
    void getStrategy_providerSansImplementation_leveUneException() {
        factory = buildFactory("CINETPAY");

        assertThatThrownBy(() -> factory.getStrategy(PaymentProviderType.WAVE_CI))
                .isInstanceOf(PaymentProviderException.class);
    }
}
