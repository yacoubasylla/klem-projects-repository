package com.klem.coreapi.tenant.application.service;

import com.klem.coreapi.tenant.application.port.TenantRepository;
import com.klem.coreapi.tenant.domain.event.TenantCreatedEvent;
import com.klem.coreapi.tenant.domain.event.TenantStatusChangedEvent;
import com.klem.coreapi.tenant.domain.exception.TenantNotFoundException;
import com.klem.coreapi.tenant.domain.model.Tenant;
import com.klem.coreapi.tenant.domain.model.TenantStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Cas d'usage du domaine {@code tenant} — transactions définies ici, jamais dans le controller
 * ({@code KLEM_MASTER_SYSTEM_DIRECTIVE.md} §5).
 */
@Service
@Transactional(readOnly = true)
public class TenantService {

    private final TenantRepository tenantRepository;
    private final ApplicationEventPublisher events;

    public TenantService(TenantRepository tenantRepository, ApplicationEventPublisher events) {
        this.tenantRepository = tenantRepository;
        this.events = events;
    }

    @Transactional
    public Tenant createTenant(String name, String sector) {
        Tenant tenant = Tenant.create(name, sector);
        tenantRepository.save(tenant);
        events.publishEvent(new TenantCreatedEvent(tenant.getId(), tenant.getName(), Instant.now()));
        return tenant;
    }

    public Tenant getTenant(UUID tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new TenantNotFoundException(tenantId));
    }

    /**
     * Vérification d'existence exposée aux autres domaines (ex. {@code identity} avant une
     * invitation) — volontairement plus étroite que {@link #getTenant}, qui expose l'entité
     * {@code Tenant} et lève {@link TenantNotFoundException}, tous deux internes au domaine
     * {@code tenant} (voir {@code PackageBoundaryRulesTest}). Ne pas remplacer un appel externe à
     * cette méthode par {@link #getTenant} pour « simplifier » — ce serait une fuite d'internals.
     */
    public boolean tenantExists(UUID tenantId) {
        return tenantRepository.findById(tenantId).isPresent();
    }

    @Transactional
    public Tenant changeStatus(UUID tenantId, TenantStatus newStatus) {
        Tenant tenant = getTenant(tenantId);
        TenantStatus previousStatus = tenant.getStatus();
        tenant.changeStatus(newStatus);
        tenantRepository.save(tenant);
        if (previousStatus != newStatus) {
            events.publishEvent(new TenantStatusChangedEvent(tenantId, previousStatus, newStatus, Instant.now()));
        }
        return tenant;
    }
}
