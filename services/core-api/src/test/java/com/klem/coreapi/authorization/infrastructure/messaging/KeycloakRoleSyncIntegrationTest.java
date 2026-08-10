package com.klem.coreapi.authorization.infrastructure.messaging;

import com.klem.coreapi.authorization.domain.model.RoleCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <b>Ce test n'a jamais pu être exécuté dans aucun environnement disponible pendant son
 * écriture</b> — contrairement aux autres tests Testcontainers du dépôt (patron identique déjà
 * éprouvé sur PostgreSQL/Kafka), le bootstrap Keycloak ci-dessous est écrit à partir de la
 * documentation de l'API Admin Keycloak, pas vérifié empiriquement. À exécuter en priorité, et à
 * corriger si nécessaire, dès qu'un environnement avec Docker à jour (API ≥ 1.40) est disponible —
 * ne pas le considérer comme une preuve de bon fonctionnement tant que ça n'a pas été fait.
 * <p>
 * Le royaume construit ici (rôles, client de provisionnement, permissions) est une
 * <b>hypothèse</b> de ce à quoi devrait ressembler le royaume Keycloak réel de KLEM DataSphere —
 * pas une copie de sa configuration effective, qui n'existe pas encore. Ce test prouve que
 * {@link KeycloakRoleSyncClient} sait dialoguer avec un Keycloak réel structuré ainsi ; il ne
 * prouve pas que c'est ainsi que le royaume réel sera structuré.
 */
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class KeycloakRoleSyncIntegrationTest {

    private static final String REALM = "klem-datasphere-test";
    private static final String PROVISIONING_CLIENT_ID = "core-api-provisioning";
    private static final String PROVISIONING_CLIENT_SECRET = "test-secret";
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    static GenericContainer<?> keycloak = new GenericContainer<>(DockerImageName.parse("quay.io/keycloak/keycloak:26.0.7"))
            .withCommand("start-dev")
            .withEnv("KEYCLOAK_ADMIN", ADMIN_USER)
            .withEnv("KEYCLOAK_ADMIN_PASSWORD", ADMIN_PASSWORD)
            .withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/realms/master").forStatusCode(200).withStartupTimeout(Duration.ofMinutes(2)));

    private static String keycloakBaseUrl;
    private static UUID testUserId;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        keycloakBaseUrl = "http://" + keycloak.getHost() + ":" + keycloak.getMappedPort(8080);
        registry.add("keycloak.admin.base-url", () -> keycloakBaseUrl);
        registry.add("keycloak.admin.realm", () -> REALM);
        registry.add("keycloak.admin.client-id", () -> PROVISIONING_CLIENT_ID);
        registry.add("keycloak.admin.client-secret", () -> PROVISIONING_CLIENT_SECRET);
    }

    @BeforeAll
    static void bootstrapTestRealm() {
        RestClient admin = RestClient.builder().baseUrl(keycloakBaseUrl).build();
        String masterAdminToken = fetchMasterAdminToken(admin);

        createRealm(admin, masterAdminToken);
        for (RoleCode roleCode : RoleCode.values()) {
            createRealmRole(admin, masterAdminToken, roleCode.name());
        }
        String provisioningClientUuid = createProvisioningClient(admin, masterAdminToken);
        String serviceAccountUserId = fetchServiceAccountUserId(admin, masterAdminToken, provisioningClientUuid);
        grantManageUsersToServiceAccount(admin, masterAdminToken, serviceAccountUserId);
        testUserId = createTestUser(admin, masterAdminToken);
    }

    @Autowired
    private KeycloakRoleSyncClient keycloakRoleSyncClient;

    @Test
    void assignRole_then_removeRole_round_trip_against_a_real_keycloak() {
        keycloakRoleSyncClient.assignRole(testUserId.toString(), RoleCode.CHAUFFEUR);
        assertThat(currentRealmRoleNames(testUserId)).contains("CHAUFFEUR");

        keycloakRoleSyncClient.removeRole(testUserId.toString(), RoleCode.CHAUFFEUR);
        assertThat(currentRealmRoleNames(testUserId)).doesNotContain("CHAUFFEUR");
    }

    // ── Bootstrap — API Admin Keycloak, non vérifié empiriquement (voir Javadoc de la classe) ────

    private static String fetchMasterAdminToken(RestClient admin) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", "admin-cli");
        form.add("username", ADMIN_USER);
        form.add("password", ADMIN_PASSWORD);

        Map<String, Object> response = admin.post()
                .uri("/realms/master/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);
        return (String) response.get("access_token");
    }

    private static void createRealm(RestClient admin, String token) {
        admin.post()
                .uri("/admin/realms")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("realm", REALM, "enabled", true))
                .retrieve()
                .toBodilessEntity();
    }

    private static void createRealmRole(RestClient admin, String token, String roleName) {
        admin.post()
                .uri("/admin/realms/{realm}/roles", REALM)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("name", roleName))
                .retrieve()
                .toBodilessEntity();
    }

    private static String createProvisioningClient(RestClient admin, String token) {
        admin.post()
                .uri("/admin/realms/{realm}/clients", REALM)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "clientId", PROVISIONING_CLIENT_ID,
                        "secret", PROVISIONING_CLIENT_SECRET,
                        "serviceAccountsEnabled", true,
                        "publicClient", false,
                        "protocol", "openid-connect"
                ))
                .retrieve()
                .toBodilessEntity();

        List<Map<String, Object>> clients = admin.get()
                .uri("/admin/realms/{realm}/clients?clientId={clientId}", REALM, PROVISIONING_CLIENT_ID)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(List.class);
        return (String) clients.get(0).get("id");
    }

    private static String fetchServiceAccountUserId(RestClient admin, String token, String provisioningClientUuid) {
        Map<String, Object> serviceAccountUser = admin.get()
                .uri("/admin/realms/{realm}/clients/{clientUuid}/service-account-user", REALM, provisioningClientUuid)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(Map.class);
        return (String) serviceAccountUser.get("id");
    }

    private static void grantManageUsersToServiceAccount(RestClient admin, String token, String serviceAccountUserId) {
        List<Map<String, Object>> realmManagementClients = admin.get()
                .uri("/admin/realms/{realm}/clients?clientId=realm-management", REALM)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(List.class);
        String realmManagementClientUuid = (String) realmManagementClients.get(0).get("id");

        Map<String, Object> manageUsersRole = admin.get()
                .uri("/admin/realms/{realm}/clients/{clientUuid}/roles/manage-users", REALM, realmManagementClientUuid)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(Map.class);

        admin.post()
                .uri("/admin/realms/{realm}/users/{userId}/role-mappings/clients/{clientUuid}",
                        REALM, serviceAccountUserId, realmManagementClientUuid)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(List.of(manageUsersRole))
                .retrieve()
                .toBodilessEntity();
    }

    private static UUID createTestUser(RestClient admin, String token) {
        admin.post()
                .uri("/admin/realms/{realm}/users", REALM)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("username", "pilote-chauffeur", "enabled", true))
                .retrieve()
                .toBodilessEntity();

        List<Map<String, Object>> users = admin.get()
                .uri("/admin/realms/{realm}/users?username=pilote-chauffeur", REALM)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(List.class);
        return UUID.fromString((String) users.get(0).get("id"));
    }

    @SuppressWarnings("unchecked")
    private List<String> currentRealmRoleNames(UUID userId) {
        RestClient admin = RestClient.builder().baseUrl(keycloakBaseUrl).build();
        String token = fetchMasterAdminToken(admin);
        List<Map<String, Object>> roles = admin.get()
                .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", REALM, userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(List.class);
        return roles.stream().map(r -> (String) r.get("name")).toList();
    }
}
