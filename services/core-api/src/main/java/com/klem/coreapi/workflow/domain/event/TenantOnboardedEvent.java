package com.klem.coreapi.workflow.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Signal composite publié une fois les trois étapes de l'onboarding réussies. Volontairement
 * {@code non} écouté par {@code audit} : les événements granulaires
 * ({@code TenantCreatedEvent}, {@code UserInvitedEvent}, {@code RoleAssignedEvent}) sont déjà
 * capturés individuellement pendant l'orchestration — ajouter celui-ci à l'écoute d'audit
 * dupliquerait la même action réelle en quatre entrées au lieu de trois. Utile en revanche pour un
 * futur pont Kafka qui voudrait un événement "métier" unique plutôt que la séquence technique.
 */
public record TenantOnboardedEvent(UUID tenantId, UUID userId, Instant occurredAt) {
}
