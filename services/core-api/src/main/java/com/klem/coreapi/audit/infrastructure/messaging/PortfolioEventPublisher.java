package com.klem.coreapi.audit.infrastructure.messaging;

import com.klem.coreapi.authorization.domain.event.RoleAssignedEvent;
import com.klem.coreapi.authorization.domain.event.RoleRevokedEvent;
import com.klem.coreapi.identity.domain.event.UserActivatedEvent;
import com.klem.coreapi.identity.domain.event.UserInvitedEvent;
import com.klem.coreapi.tenant.domain.event.TenantCreatedEvent;
import com.klem.coreapi.tenant.domain.event.TenantStatusChangedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.UUID;

/**
 * Pont vers le cluster Kafka partagé du portefeuille — écoute les mêmes événements de domaine
 * qu'{@code AuditService}, indépendamment (deux abonnés séparés à la même publication in-process,
 * pas un appel de l'un vers l'autre). Vit dans {@code audit.infrastructure.messaging} : c'est le
 * seul domaine déjà autorisé à dépendre des {@code domain.event} des autres
 * ({@code PackageBoundaryRulesTest} règle 2), le rattachement ici ne demande donc aucune nouvelle
 * exemption de frontière.
 * <p>
 * Topics : nom de topic = {@code eventType} (ex. {@code tenant.created}), cohérent avec la
 * convention déjà en usage pour {@code audit.event.logged}/{@code audit.status.changed}
 * (topics transverses communs, {@code shared_architecture/data_pipeline/specifications_techniques.md}
 * §2.2) — {@code tenant}/{@code user}/{@code roleAssignment} sont des primitives de plateforme
 * consommées par tous les produits DataSphere, pas un domaine métier d'un seul projet, donc pas de
 * préfixe {@code core.*} par produit ici.
 */
@Component
public class PortfolioEventPublisher {

    private static final String SOURCE = "core-api";
    private static final String SCHEMA_VERSION = "1.0";

    private final KafkaTemplate<String, PortfolioEvent> kafkaTemplate;

    public PortfolioEventPublisher(KafkaTemplate<String, PortfolioEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(TenantCreatedEvent event) {
        publish("tenant.created", event.eventId(), "tenant", event.tenantId(), event.tenantId(),
                event.occurredAt(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(TenantStatusChangedEvent event) {
        publish("tenant.status.changed", event.eventId(), "tenant", event.tenantId(), event.tenantId(),
                event.occurredAt(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserInvitedEvent event) {
        publish("user.invited", event.eventId(), "user", event.tenantId(), event.userId(),
                event.occurredAt(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserActivatedEvent event) {
        publish("user.activated", event.eventId(), "user", null, event.userId(), event.occurredAt(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleAssignedEvent event) {
        publish("role.assigned", event.eventId(), "roleAssignment", event.tenantId(), event.userId(),
                event.occurredAt(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleRevokedEvent event) {
        publish("role.revoked", event.eventId(), "roleAssignment", event.tenantId(), event.userId(),
                event.occurredAt(), event);
    }

    private void publish(String eventType, UUID eventId, String aggregateType, UUID tenantId,
                          UUID aggregateId, Instant occurredAt, Object payload) {
        PortfolioEvent envelope = new PortfolioEvent(
                eventId.toString(),
                eventType,
                SOURCE,
                occurredAt,
                aggregateType,
                aggregateId.toString(),
                null,
                payload,
                new PortfolioEvent.Metadata(null, null, tenantId == null ? null : tenantId.toString()),
                SCHEMA_VERSION
        );
        // Clé = aggregateId : garantit l'ordre des messages relatifs à un même tenant/utilisateur
        // au sein d'une même partition, sans exiger l'ordre global entre agrégats différents.
        kafkaTemplate.send(eventType, aggregateId.toString(), envelope);
    }
}
