package com.klem.coreapi.authorization.infrastructure.persistence;

import com.klem.coreapi.authorization.application.port.RoleAssignmentRepository;
import com.klem.coreapi.authorization.domain.model.RoleAssignment;
import com.klem.coreapi.authorization.domain.model.RoleCode;
import com.klem.coreapi.identity.application.port.UserRepository;
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
class AuthorizationJpaRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private RoleAssignmentRepository roleAssignmentRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_find_and_delete_role_assignment() {
        Tenant tenant = Tenant.create("Fleet-Advance pilote", "logistique");
        tenantRepository.save(tenant);
        User user = User.provisioned("kc-chauffeur-1", "chauffeur@klem.tech", "Chauffeur pilote");
        userRepository.save(user);

        RoleAssignment assignment = RoleAssignment.grant(tenant.getId(), user.getId(), RoleCode.CHAUFFEUR);
        roleAssignmentRepository.save(assignment);

        List<RoleAssignment> forTenant = roleAssignmentRepository.findByTenantId(tenant.getId());
        assertThat(forTenant).hasSize(1);

        Optional<RoleAssignment> found = roleAssignmentRepository
                .findByTenantIdAndUserIdAndRoleCode(tenant.getId(), user.getId(), RoleCode.CHAUFFEUR);
        assertThat(found).isPresent();

        roleAssignmentRepository.delete(found.get());
        assertThat(roleAssignmentRepository.findByTenantId(tenant.getId())).isEmpty();
    }
}
