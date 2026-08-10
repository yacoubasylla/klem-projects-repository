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
 * Une valeur au sein d'un {@link ReferentialList} (ex. {@code CI} / « Côte d'Ivoire » dans la liste
 * {@code countries}). {@code listId} référencé par identifiant, pas par relation JPA
 * {@code @ManyToOne} — requête explicite côté service plutôt qu'un chargement implicite
 * (`KLEM_MASTER_SYSTEM_DIRECTIVE.md` §8 : éviter les relations EAGER, préférer les requêtes
 * explicites).
 */
@Entity
@Table(name = "referential_entry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReferentialEntry {

    @Id
    private UUID id;

    @Column(name = "list_id", nullable = false)
    private UUID listId;

    @Column(name = "entry_code", nullable = false)
    private String entryCode;

    @Column(nullable = false)
    private String label;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    private ReferentialEntry(UUID id, UUID listId, String entryCode, String label, int sortOrder) {
        this.id = id;
        this.listId = listId;
        this.entryCode = entryCode;
        this.label = label;
        this.sortOrder = sortOrder;
    }

    public static ReferentialEntry create(UUID listId, String entryCode, String label, int sortOrder) {
        return new ReferentialEntry(UUID.randomUUID(), listId, entryCode, label, sortOrder);
    }
}
