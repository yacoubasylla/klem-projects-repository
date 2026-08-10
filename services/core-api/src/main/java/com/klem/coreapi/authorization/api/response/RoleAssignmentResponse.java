package com.klem.coreapi.authorization.api.response;

import com.klem.coreapi.authorization.domain.model.RoleAssignment;
import com.klem.coreapi.authorization.domain.model.RoleCode;

import java.time.Instant;
import java.util.UUID;

public record RoleAssignmentResponse(UUID userId, RoleCode roleCode, Instant grantedAt) {

    public static RoleAssignmentResponse from(RoleAssignment assignment) {
        return new RoleAssignmentResponse(assignment.getUserId(), assignment.getRoleCode(), assignment.getGrantedAt());
    }
}
