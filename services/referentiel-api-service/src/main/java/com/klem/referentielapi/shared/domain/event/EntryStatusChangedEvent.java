package com.klem.referentielapi.shared.domain.event;

import com.klem.referentielapi.shared.domain.StatutPublication;

import java.time.Instant;
import java.util.UUID;

/** Événement générique publié par les 4 domaines à chaque transition de statut validée. */
public record EntryStatusChangedEvent(
        UUID eventId,
        String aggregateType,
        UUID aggregateId,
        String code,
        StatutPublication previousStatus,
        StatutPublication newStatus,
        String actorSubject,
        Instant occurredAt
) {
}
