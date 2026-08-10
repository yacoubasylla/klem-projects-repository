package com.klem.coreapi.authorization.infrastructure.persistence;

import com.klem.coreapi.authorization.application.port.RoleAssignmentRepository;
import com.klem.coreapi.authorization.domain.model.RoleAssignment;
import com.klem.coreapi.authorization.domain.model.RoleCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface RoleAssignmentJpaRepository extends JpaRepository<RoleAssignment, UUID>, RoleAssignmentRepository {

    @Override
    List<RoleAssignment> findByTenantId(UUID tenantId);

    @Override
    List<RoleAssignment> findByUserId(UUID userId);

    @Override
    Optional<RoleAssignment> findByTenantIdAndUserIdAndRoleCode(UUID tenantId, UUID userId, RoleCode roleCode);
}
