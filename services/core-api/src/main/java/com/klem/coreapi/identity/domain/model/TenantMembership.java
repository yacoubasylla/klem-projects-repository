package com.klem.coreapi.identity.domain.model;

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

/**
 * Rattachement d'un {@link User} à un tenant — un utilisateur peut appartenir à plusieurs tenants
 * (README.md §1). Table d'association porteuse d'état : ne peut pas être un simple
 * {@code @ManyToMany} sans attributs.
 */
@Entity
@Table(name = "tenant_membership")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TenantMembership {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MembershipStatus status;

    @Column(name = "invited_at", nullable = false)
    private Instant invitedAt;

    @Column(name = "activated_at")
    private Instant activatedAt;

    private TenantMembership(UUID id, UUID userId, UUID tenantId, MembershipStatus status, Instant invitedAt) {
        this.id = id;
        this.userId = userId;
        this.tenantId = tenantId;
        this.status = status;
        this.invitedAt = invitedAt;
    }

    public static TenantMembership invite(UUID userId, UUID tenantId) {
        return new TenantMembership(UUID.randomUUID(), userId, tenantId, MembershipStatus.INVITED, Instant.now());
    }

    public void activate() {
        if (status != MembershipStatus.ACTIVE) {
            this.status = MembershipStatus.ACTIVE;
            this.activatedAt = Instant.now();
        }
    }
}
