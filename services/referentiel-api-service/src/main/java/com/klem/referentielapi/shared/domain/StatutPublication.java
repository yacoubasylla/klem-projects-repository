package com.klem.referentielapi.shared.domain;

import java.util.Set;

/**
 * Cycle de vie éditorial commun aux quatre entités du référentiel (texte réglementaire, procédure,
 * document requis, opération commerce) — voir
 * {@code klem-labs-repository/projects/08_klem_trade_x/specifications_fonctionnelles.md} §4.3 et
 * {@code specifications_techniques.md} §2.1. Une fiche est toujours créée {@code PROPOSEE} (que ce
 * soit par un {@code Editeur} humain ou, plus tard, par l'agent {@code klem_ref_bot}) — jamais
 * publiée directement, conformément à la règle de validation humaine systématique.
 */
public enum StatutPublication {
    PROPOSEE,
    EN_REVISION,
    PUBLIEE,
    REJETEE;

    /**
     * Transitions valides — volontairement restrictives plutôt que libres (contrairement à
     * {@code TenantStatus} sur {@code core-api}, qui autorise tout changement) : ce statut porte un
     * vrai ordre métier (proposition → revue → publication/rejet), pas un simple drapeau.
     */
    private static final Set<StatutPublication> FROM_PROPOSEE = Set.of(EN_REVISION, REJETEE);
    private static final Set<StatutPublication> FROM_EN_REVISION = Set.of(PUBLIEE, REJETEE, PROPOSEE);
    private static final Set<StatutPublication> FROM_PUBLIEE = Set.of();
    private static final Set<StatutPublication> FROM_REJETEE = Set.of(PROPOSEE);

    public boolean canTransitionTo(StatutPublication target) {
        return switch (this) {
            case PROPOSEE -> FROM_PROPOSEE.contains(target);
            case EN_REVISION -> FROM_EN_REVISION.contains(target);
            case PUBLIEE -> FROM_PUBLIEE.contains(target);
            case REJETEE -> FROM_REJETEE.contains(target);
        };
    }
}
