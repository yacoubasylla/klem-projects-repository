package com.klem.coreapi.audit.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klem.coreapi.audit.application.port.AuditEntryRepository;
import com.klem.coreapi.audit.domain.model.AuditEntry;
import com.klem.coreapi.authorization.domain.event.RoleAssignedEvent;
import com.klem.coreapi.authorization.domain.event.RoleRevokedEvent;
import com.klem.coreapi.identity.domain.event.UserActivatedEvent;
import com.klem.coreapi.identity.domain.event.UserInvitedEvent;
import com.klem.coreapi.tenant.domain.event.TenantCreatedEvent;
import com.klem.coreapi.tenant.domain.event.TenantStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

/**
 * Domaine {@code audit} — consommateur des événements de domaine des autres domaines, jamais
 * l'inverse. Dépend de leurs classes {@code domain.event} (contrat d'intégration intentionnel de
 * chaque domaine — voir {@code PackageBoundaryRulesTest}, règle assouplie spécifiquement pour ce
 * sous-package), **jamais** de leur {@code domain.model}/{@code application}/{@code infrastructure}.
 * <p>
 * {@link TransactionalEventListener} avec {@link TransactionPhase#AFTER_COMMIT} : une entrée n'est
 * journalisée que si la transaction qui a publié l'événement a effectivement validé — un
 * {@code createTenant} qui échoue en base ne doit jamais produire d'entrée d'audit.
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditEntryRepository auditEntryRepository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditEntryRepository auditEntryRepository, ObjectMapper objectMapper) {
        this.auditEntryRepository = auditEntryRepository;
        this.objectMapper = objectMapper;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(TenantCreatedEvent event) {
        capture(event.eventId(), "tenant.created", event.tenantId(), event.tenantId(), event.occurredAt(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(TenantStatusChangedEvent event) {
        capture(event.eventId(), "tenant.status.changed", event.tenantId(), event.tenantId(), event.occurredAt(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserInvitedEvent event) {
        capture(event.eventId(), "user.invited", event.tenantId(), event.userId(), event.occurredAt(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserActivatedEvent event) {
        // Pas de tenantId : l'activation d'un compte n'est pas scopée à un tenant particulier
        // (un utilisateur peut appartenir à plusieurs tenants) — voir README.md identity §1.
        capture(event.eventId(), "user.activated", null, event.userId(), event.occurredAt(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleAssignedEvent event) {
        capture(event.eventId(), "role.assigned", event.tenantId(), event.userId(), event.occurredAt(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleRevokedEvent event) {
        capture(event.eventId(), "role.revoked", event.tenantId(), event.userId(), event.occurredAt(), event);
    }

    public Page<AuditEntry> getEntries(UUID tenantId, Pageable pageable) {
        return auditEntryRepository.findByTenantId(tenantId, pageable);
    }

    private void capture(UUID eventId, String eventType, UUID tenantId, UUID aggregateId,
                          java.time.Instant occurredAt, Object event) {
        String payload = serialize(event);
        AuditEntry entry = AuditEntry.capture(eventId, eventType, tenantId, aggregateId, occurredAt, payload);
        auditEntryRepository.save(entry);
    }

    private String serialize(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            // Ne jamais faire échouer la capture d'audit pour un problème de sérialisation —
            // dégrader avec un payload minimal plutôt que de perdre la trace de l'événement lui-même.
            log.warn("Échec de sérialisation du payload d'audit pour {}", event.getClass().getSimpleName(), e);
            return "{\"error\":\"serialization_failed\"}";
        }
    }
}
