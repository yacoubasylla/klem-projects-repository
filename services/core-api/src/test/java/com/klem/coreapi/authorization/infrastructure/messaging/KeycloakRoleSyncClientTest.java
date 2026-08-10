package com.klem.coreapi.authorization.infrastructure.messaging;

import com.klem.coreapi.authorization.domain.model.RoleCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * {@link MockRestServiceServer} vérifie la séquence exacte d'appels HTTP — pas un vrai Keycloak
 * (voir {@code KeycloakRoleSyncIntegrationTest} pour ça). Ce test prouve que le client construit
 * les bonnes requêtes contre l'API décrite dans la documentation Keycloak officielle ; il ne prouve
 * pas que cette API se comporte ainsi pour de vrai (c'est le rôle du test d'intégration).
 */
class KeycloakRoleSyncClientTest {

    private MockRestServiceServer mockServer;
    private KeycloakRoleSyncClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        KeycloakAdminProperties properties = new KeycloakAdminProperties(
                "http://keycloak.test", "klem-datasphere", "core-api-provisioning", "s3cr3t");
        client = new KeycloakRoleSyncClient(builder, properties);
    }

    @Test
    void assignRole_fetches_token_then_resolves_role_then_posts_role_mapping() {
        expectTokenRequest();
        mockServer.expect(requestTo("http://keycloak.test/admin/realms/klem-datasphere/roles/CHAUFFEUR"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer tok-123"))
                .andRespond(withSuccess("""
                        {"id": "role-uuid-1", "name": "CHAUFFEUR"}
                        """, MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo("http://keycloak.test/admin/realms/klem-datasphere/users/kc-user-1/role-mappings/realm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer tok-123"))
                .andExpect(content().json("[{\"id\":\"role-uuid-1\",\"name\":\"CHAUFFEUR\"}]"))
                .andRespond(withNoContent());

        client.assignRole("kc-user-1", RoleCode.CHAUFFEUR);

        mockServer.verify();
    }

    @Test
    void removeRole_sends_delete_with_role_mapping_body() {
        expectTokenRequest();
        mockServer.expect(requestTo("http://keycloak.test/admin/realms/klem-datasphere/roles/ADMIN"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"id": "role-uuid-2", "name": "ADMIN"}
                        """, MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo("http://keycloak.test/admin/realms/klem-datasphere/users/kc-user-2/role-mappings/realm"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(content().json("[{\"id\":\"role-uuid-2\",\"name\":\"ADMIN\"}]"))
                .andRespond(withNoContent());

        client.removeRole("kc-user-2", RoleCode.ADMIN);

        mockServer.verify();
    }

    @Test
    void assignRole_reuses_cached_token_across_calls() {
        // Un seul appel au endpoint de token enregistré : si le client en refaisait un second,
        // MockRestServiceServer échouerait faute d'attente correspondante — c'est la preuve du cache.
        expectTokenRequest();
        mockServer.expect(requestTo("http://keycloak.test/admin/realms/klem-datasphere/roles/CLIENT"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\": \"role-uuid-3\", \"name\": \"CLIENT\"}", MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo("http://keycloak.test/admin/realms/klem-datasphere/users/kc-user-3/role-mappings/realm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withNoContent());
        mockServer.expect(requestTo("http://keycloak.test/admin/realms/klem-datasphere/roles/OPERATEUR"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\": \"role-uuid-4\", \"name\": \"OPERATEUR\"}", MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo("http://keycloak.test/admin/realms/klem-datasphere/users/kc-user-3/role-mappings/realm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withNoContent());

        client.assignRole("kc-user-3", RoleCode.CLIENT);
        client.assignRole("kc-user-3", RoleCode.OPERATEUR);

        mockServer.verify();
    }

    private void expectTokenRequest() {
        mockServer.expect(requestTo("http://keycloak.test/realms/klem-datasphere/protocol/openid-connect/token"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("client_id=core-api-provisioning")))
                .andRespond(withSuccess("""
                        {"access_token": "tok-123", "expires_in": 300}
                        """, MediaType.APPLICATION_JSON));
    }
}
