package com.klem.referentielapi.shared.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Événement générique publié par les 4 domaines du référentiel (mêmes champs de base, même
 * workflow {@code StatutPublication}) à la création d'une fiche — évite de dupliquer 4 classes
 * d'événements quasi identiques.
 */
public record EntryProposedEvent(
        UUID eventId,
        String aggregateType,
        UUID aggregateId,
        String code,
        String createdBy,
        Instant occurredAt
) {
}
