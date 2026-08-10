package com.klem.coreapi.workflow.application.service;

import com.klem.coreapi.authorization.application.port.RoleAssignmentRepository;
import com.klem.coreapi.authorization.domain.model.RoleCode;
import com.klem.coreapi.identity.application.port.TenantMembershipRepository;
import com.klem.coreapi.identity.domain.model.MembershipStatus;
import com.klem.coreapi.tenant.application.port.TenantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Contrairement aux autres domaines, ce test câble les VRAIS {@code TenantService}/
 * {@code IdentityService}/{@code AuthorizationService} (pas de mock) — c'est la seule façon de
 * vérifier que l'orchestration produit un état cohérent dans les trois domaines à la fois contre un
 * PostgreSQL réel. Pas de test de rollback ici : forcer un échec déterministe à la 3ᵉ étape sans
 * mock aurait exigé un scénario artificiel plus trompeur qu'utile ; la garantie de rollback elle-
 * même repose sur {@code @Transactional} (comportement Spring déjà établi), vérifiée plus
 * directement par {@code WorkflowServiceTest} (propagation de l'exception).
 */
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class WorkflowServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private TenantMembershipRepository tenantMembershipRepository;

    @Autowired
    private RoleAssignmentRepository roleAssignmentRepository;

    @Test
    void onboardTenant_persists_consistent_state_across_the_three_domains() {
        WorkflowService.TenantOnboardingResult result = workflowService.onboardTenant(
                "Boutiki pilote", "commerce informel", "admin@klem.tech", "Admin Boutiki");

        assertThat(tenantRepository.findById(result.tenant().getId())).isPresent();

        Optional<com.klem.coreapi.identity.domain.model.TenantMembership> membership =
                tenantMembershipRepository.findByUserIdAndTenantId(result.membership().getUserId(), result.tenant().getId());
        assertThat(membership).isPresent();
        assertThat(membership.get().getStatus()).isEqualTo(MembershipStatus.INVITED);

        assertThat(roleAssignmentRepository.findByTenantIdAndUserIdAndRoleCode(
                result.tenant().getId(), result.membership().getUserId(), RoleCode.ADMIN)).isPresent();
    }
}
