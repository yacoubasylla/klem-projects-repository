package com.klem.coreapi.tenant.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Un tenant/organisation KLEM DataSphere — voir {@code services/core-api/README.md} §1.
 * Registre central : ne porte pas lui-même de {@code tenant_id} (il EST le référentiel de tenants),
 * à la différence des entités des autres domaines qui devront s'y rattacher.
 */
@Entity
@Table(name = "tenant")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) // JPA uniquement — jamais d'instanciation directe hors #create
public class Tenant {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column
    private String sector;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TenantStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private Tenant(UUID id, String name, String sector, TenantStatus status, Instant now) {
        this.id = id;
        this.name = name;
        this.sector = sector;
        this.status = status;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static Tenant create(String name, String sector) {
        Instant now = Instant.now();
        return new Tenant(UUID.randomUUID(), name, sector, TenantStatus.PENDING, now);
    }

    public void changeStatus(TenantStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }
}
