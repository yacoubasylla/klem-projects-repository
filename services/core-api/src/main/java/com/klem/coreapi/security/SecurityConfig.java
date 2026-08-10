package com.klem.coreapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * OAuth2 Resource Server (JWT), conforme à KLEM_MASTER_SYSTEM_DIRECTIVE.md §7.
 * `issuer-uri` provient de la variable d'environnement OIDC_ISSUER_URI (voir application.yml) —
 * jamais codée en dur. core-api ne réimplémente pas OAuth2/OIDC : Keycloak reste l'IdP
 * (shared_architecture/identity_&_iam/), ce service ne fait que valider le JWT émis.
 * <p>
 * <b>Contrat JWT attendu</b> (voir ADR
 * {@code 2026-08-10-autorisation-core-api-claims-jwt-vs-appel-synchrone.md}) : une claim
 * {@code roles} (tableau de chaînes, ex. {@code ["PLATFORM_ADMIN"]}) projetée par un protocol
 * mapper Keycloak à configurer par royaume. Ce mapper n'est pas encore posé — hypothèse à valider
 * dès qu'un royaume Keycloak réel existe pour core-api, pas un fait déjà vérifié.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
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
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    private Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}
