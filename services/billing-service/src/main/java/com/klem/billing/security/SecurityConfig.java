package com.klem.billing.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * OAuth2 Resource Server (JWT), conforme à MASTER_SYSTEM_DIRECTIVE.md §7 — service-à-service via le
 * socle IAM (identity_&_iam), voir specifications_techniques.md §6.
 *
 * `/webhooks/**` reste public au sens Spring Security : les opérateurs/agrégateurs externes ne
 * portent pas de jeton OAuth2 KLEM. Leur authenticité est garantie par la vérification de signature
 * propre à chaque {@link com.klem.billing.application.port.PaymentProvider} (HMAC ou schéma
 * spécifique), pas par ce filtre — voir specifications_techniques.md §4.2.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
            "/webhooks/**",
            "/actuator/health",
            "/actuator/health/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                }));
        return http.build();
    }
}
