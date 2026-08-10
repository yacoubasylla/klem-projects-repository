package com.klem.coreapi.authorization.infrastructure.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {@code baseUrl} pointe la racine Keycloak (ex. {@code https://auth.klem.tech}), pas l'API admin
 * elle-même — les chemins {@code /admin/realms/...} et {@code /realms/.../protocol/openid-connect/token}
 * sont construits par {@link KeycloakRoleSyncClient}.
 * <p>
 * Hypothèse simplificatrice assumée : le client {@code core-api-provisioning} et les royaumes cibles
 * de synchronisation sont supposés être le même royaume ({@code realm}) — pas un royaume
 * d'administration séparé. À revoir si le royaume Keycloak réel de KLEM DataSphere sépare les deux
 * (voir README.md, section sur ce client).
 */
@ConfigurationProperties(prefix = "keycloak.admin")
public record KeycloakAdminProperties(String baseUrl, String realm, String clientId, String clientSecret) {
}
