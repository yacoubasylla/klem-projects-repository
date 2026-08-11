package com.klem.referentielapi.textereglementaire.domain.model;

import com.klem.referentielapi.shared.domain.InvalidStatutTransitionException;
import com.klem.referentielapi.shared.domain.StatutPublication;
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
import java.time.LocalDate;
import java.util.UUID;

/**
 * Un texte réglementaire du référentiel Trade-X (loi, décret, circulaire, note de procédure...) —
 * voir {@code klem-labs-repository/projects/08_klem_trade_x/specifications_techniques.md} §2.1.
 * Racine du référentiel : aucune dépendance vers {@code procedure}/{@code documentrequis}/
 * {@code operationcommerce} (voir {@code PackageBoundaryRulesTest}).
 */
@Entity
@Table(name = "texte_reglementaire")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA uniquement — jamais d'instanciation directe hors #propose
public class TexteReglementaire {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(name = "date_publication")
    private LocalDate datePublication;

    @Column
    private String reference;

    @Column
    private String domaine;

    @Column(name = "url_source")
    private String urlSource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutPublication statut;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "validated_by")
    private String validatedBy;

    @Column(name = "validated_at")
    private Instant validatedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private TexteReglementaire(UUID id, String titre, String type, LocalDate datePublication,
                                String reference, String domaine, String urlSource,
                                String createdBy, Instant now) {
        this.id = id;
        this.titre = titre;
        this.type = type;
        this.datePublication = datePublication;
        this.reference = reference;
        this.domaine = domaine;
        this.urlSource = urlSource;
        this.statut = StatutPublication.PROPOSEE;
        this.createdBy = createdBy;
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Toute fiche naît {@code PROPOSEE}, qu'elle soit créée par un {@code Editeur} humain ou, plus
     * tard, par l'agent {@code klem_ref_bot} — jamais publiée directement (validation humaine
     * systématique, spec §4.2).
     */
    public static TexteReglementaire propose(String titre, String type, LocalDate datePublication,
                                              String reference, String domaine, String urlSource,
                                              String createdBy) {
        return new TexteReglementaire(UUID.randomUUID(), titre, type, datePublication, reference,
                domaine, urlSource, createdBy, Instant.now());
    }

    public void changeStatus(StatutPublication newStatus, String actorSubject) {
        if (!this.statut.canTransitionTo(newStatus)) {
            throw new InvalidStatutTransitionException(this.statut, newStatus);
        }
        this.statut = newStatus;
        this.updatedAt = Instant.now();
        if (newStatus == StatutPublication.PUBLIEE || newStatus == StatutPublication.REJETEE) {
            this.validatedBy = actorSubject;
            this.validatedAt = Instant.now();
        }
    }
}
