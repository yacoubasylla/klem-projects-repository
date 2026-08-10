package com.klem.coreapi.referential.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Une catégorie de référentiel commun (ex. {@code countries}, {@code currencies}) — README.md §1 :
 * partagée entre plusieurs produits, distincte des référentiels métier spécialisés d'un seul
 * produit (TEC/codes SH → {@code referentiel-api-service}).
 */
@Entity
@Table(name = "referential_list")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReferentialList {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String label;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private ReferentialList(UUID id, String code, String label, Instant now) {
        this.id = id;
        this.code = code;
        this.label = label;
        this.createdAt = now;
    }

    public static ReferentialList create(String code, String label) {
        return new ReferentialList(UUID.randomUUID(), code, label, Instant.now());
    }
}
