package com.klem.coreapi.identity.infrastructure.persistence;

import com.klem.coreapi.identity.application.port.TenantMembershipRepository;
import com.klem.coreapi.identity.application.port.UserRepository;
import com.klem.coreapi.identity.domain.model.TenantMembership;
import com.klem.coreapi.identity.domain.model.User;
import com.klem.coreapi.tenant.application.port.TenantRepository;
import com.klem.coreapi.tenant.domain.model.Tenant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class IdentityJpaRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantMembershipRepository membershipRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void save_and_lookup_user_by_email_and_keycloak_subject() {
        User invited = User.invited("pilote@klem.tech", "Pilote Boutiki");
        userRepository.save(invited);

        assertThat(userRepository.findByEmail("pilote@klem.tech")).isPresent();
        assertThat(userRepository.findByKeycloakSubject("kc-inconnu")).isEmpty();

        invited.linkToKeycloakSubject("kc-pilote-1");
        userRepository.save(invited);

        assertThat(userRepository.findByKeycloakSubject("kc-pilote-1")).isPresent();
    }

    @Test
    void save_and_query_membership_by_user_and_tenant() {
        Tenant tenant = Tenant.create("Boutiki pilote", "commerce informel");
        tenantRepository.save(tenant);

        User user = User.invited("membre@klem.tech", "Membre");
        userRepository.save(user);

        TenantMembership membership = TenantMembership.invite(user.getId(), tenant.getId());
        membershipRepository.save(membership);

        List<TenantMembership> memberships = membershipRepository.findByUserId(user.getId());
        assertThat(memberships).hasSize(1);

        Optional<TenantMembership> found = membershipRepository.findByUserIdAndTenantId(user.getId(), tenant.getId());
        assertThat(found).isPresent();
    }
}
