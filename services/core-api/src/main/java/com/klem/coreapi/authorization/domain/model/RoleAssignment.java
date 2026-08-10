package com.klem.coreapi.authorization.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/** Attribution d'un {@link RoleCode} à un utilisateur, dans un tenant donné. */
@Entity
@Table(name = "role_assignment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleAssignment {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_code", nullable = false, length = 20)
    private RoleCode roleCode;

    @Column(name = "granted_at", nullable = false)
    private Instant grantedAt;

    private RoleAssignment(UUID id, UUID tenantId, UUID userId, RoleCode roleCode, Instant grantedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.userId = userId;
        this.roleCode = roleCode;
        this.grantedAt = grantedAt;
    }

    public static RoleAssignment grant(UUID tenantId, UUID userId, RoleCode roleCode) {
        return new RoleAssignment(UUID.randomUUID(), tenantId, userId, roleCode, Instant.now());
    }
}
