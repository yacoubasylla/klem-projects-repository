package com.klem.billing.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * `RestClient.Builder` est auto-configuré par Spring Boot (spring-boot-starter-web) — pas de bean à
 * déclarer ici, chaque provider l'injecte directement (même pattern que
 * {@code KeycloakRoleSyncClient} dans core-api).
 */
@Configuration
@EnableConfigurationProperties({AggregatorProperties.class, DirectOperatorProperties.class})
public class PaymentProviderPropertiesConfig {
}
