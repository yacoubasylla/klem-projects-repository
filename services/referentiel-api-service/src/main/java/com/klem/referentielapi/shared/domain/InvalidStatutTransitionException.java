package com.klem.referentielapi.shared.domain;

/**
 * Levée par toute entité du référentiel (texte réglementaire, procédure, document requis,
 * opération commerce) lorsqu'une transition de {@link StatutPublication} demandée n'est pas
 * autorisée par le workflow éditorial (ex. {@code PUBLIEE → EN_REVISION}) — commune aux quatre
 * domaines puisqu'elle ne dépend que du statut, pas d'une entité spécifique.
 */
public class InvalidStatutTransitionException extends ConflictException {

    public InvalidStatutTransitionException(StatutPublication current, StatutPublication target) {
        super("Transition de statut invalide : " + current + " → " + target);
    }
}
