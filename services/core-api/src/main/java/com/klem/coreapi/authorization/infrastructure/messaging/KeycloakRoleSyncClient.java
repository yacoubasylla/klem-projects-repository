package com.klem.coreapi.authorization.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.klem.coreapi.authorization.domain.model.RoleCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;

/**
 * Client REST étroit contre l'API d'administration Keycloak — pas le SDK
 * {@code org.keycloak:keycloak-admin-client} (dépendance plus lourde, surface bien plus large que
 * les 3 opérations réellement nécessaires ici : jeton client-credentials, résoudre un rôle de
 * royaume par nom, attribuer/retirer ce rôle à un utilisateur). Cohérent avec
 * {@code KLEM_MASTER_SYSTEM_DIRECTIVE.md} §1 : « limiter les dépendances inutiles ».
 * <p>
 * <b>Ce qui est vérifié par les tests de ce dépôt, et ce qui ne l'est pas</b> : le test
 * d'intégration ({@code KeycloakRoleSyncIntegrationTest}) tourne contre un vrai conteneur Keycloak
 * (Testcontainers), avec un royaume que ce test construit lui-même via l'API admin — pas une copie
 * du royaume Keycloak réel de KLEM DataSphere, qui n'existe pas encore. Il prouve donc que ce client
 * appelle correctement une API Keycloak réelle, structurée comme on peut raisonnablement s'y
 * attendre — pas que cette structure correspond au royaume qui sera effectivement déployé.
 */
@Component
public class KeycloakRoleSyncClient {

    private final RestClient restClient;
    private final KeycloakAdminProperties properties;

    private volatile String cachedAccessToken;
    private volatile Instant cachedTokenExpiresAt = Instant.EPOCH;

    public KeycloakRoleSyncClient(RestClient.Builder builder, KeycloakAdminProperties properties) {
        this.restClient = builder.baseUrl(properties.baseUrl()).build();
        this.properties = properties;
    }

    /** Attribue le rôle de royaume {@code roleCode} à l'utilisateur Keycloak {@code keycloakUserId}. */
    public void assignRole(String keycloakUserId, RoleCode roleCode) {
        RealmRole role = fetchRealmRole(roleCode);
        restClient.post()
                .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", properties.realm(), keycloakUserId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(List.of(role))
                .retrieve()
                .toBodilessEntity();
    }

    /** Retire le rôle de royaume {@code roleCode} de l'utilisateur Keycloak {@code keycloakUserId}. */
    public void removeRole(String keycloakUserId, RoleCode roleCode) {
        RealmRole role = fetchRealmRole(roleCode);
        restClient.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", properties.realm(), keycloakUserId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(List.of(role))
                .retrieve()
                .toBodilessEntity();
    }

    private RealmRole fetchRealmRole(RoleCode roleCode) {
        RealmRole role = restClient.get()
                .uri("/admin/realms/{realm}/roles/{roleName}", properties.realm(), roleCode.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken())
                .retrieve()
                .body(RealmRole.class);
        if (role == null) {
            throw new IllegalStateException(
                    "Rôle de royaume Keycloak introuvable : " + roleCode
                            + " — le royaume doit définir un rôle par valeur de RoleCode (voir README.md).");
        }
        return role;
    }

    /**
     * Jeton client-credentials, mis en cache jusqu'à 30s avant expiration (marge conservative
     * plutôt que d'attendre l'échec d'un appel Admin API pour rafraîchir).
     */
    private synchronized String accessToken() {
        if (cachedAccessToken != null && Instant.now().isBefore(cachedTokenExpiresAt)) {
            return cachedAccessToken;
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", properties.clientId());
        form.add("client_secret", properties.clientSecret());

        TokenResponse response = restClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", properties.realm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(TokenResponse.class);

        if (response == null) {
            throw new IllegalStateException("Réponse vide du endpoint de token Keycloak.");
        }

        cachedAccessToken = response.accessToken();
        cachedTokenExpiresAt = Instant.now().plusSeconds(Math.max(response.expiresIn() - 30, 0));
        return cachedAccessToken;
    }

    private record RealmRole(String id, String name) {
    }

    private record TokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("expires_in") long expiresIn
    ) {
    }
}
