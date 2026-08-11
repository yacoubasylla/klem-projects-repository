package com.klem.referentielapi.textereglementaire.api.response;

import com.klem.referentielapi.shared.domain.StatutPublication;
import com.klem.referentielapi.textereglementaire.domain.model.TexteReglementaire;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Jamais l'entité {@link TexteReglementaire} elle-même n'est renvoyée par le controller. */
public record TexteReglementaireResponse(
        UUID id,
        String titre,
        String type,
        LocalDate datePublication,
        String reference,
        String domaine,
        String urlSource,
        StatutPublication statut,
        String createdBy,
        String validatedBy,
        Instant validatedAt,
        Instant createdAt
) {

    public static TexteReglementaireResponse from(TexteReglementaire texte) {
        return new TexteReglementaireResponse(
                texte.getId(),
                texte.getTitre(),
                texte.getType(),
                texte.getDatePublication(),
                texte.getReference(),
                texte.getDomaine(),
                texte.getUrlSource(),
                texte.getStatut(),
                texte.getCreatedBy(),
                texte.getValidatedBy(),
                texte.getValidatedAt(),
                texte.getCreatedAt()
        );
    }
}
