package com.klem.coreapi.authorization.application.port;

import com.klem.coreapi.authorization.domain.model.RoleAssignment;
import com.klem.coreapi.authorization.domain.model.RoleCode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleAssignmentRepository {

    RoleAssignment save(RoleAssignment assignment);

    void delete(RoleAssignment assignment);

    List<RoleAssignment> findByTenantId(UUID tenantId);

    List<RoleAssignment> findByUserId(UUID userId);

    Optional<RoleAssignment> findByTenantIdAndUserIdAndRoleCode(UUID tenantId, UUID userId, RoleCode roleCode);
}
