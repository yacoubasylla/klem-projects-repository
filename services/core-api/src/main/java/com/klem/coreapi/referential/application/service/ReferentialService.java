package com.klem.coreapi.referential.application.service;

import com.klem.coreapi.referential.application.port.ReferentialEntryRepository;
import com.klem.coreapi.referential.application.port.ReferentialListRepository;
import com.klem.coreapi.referential.domain.exception.ReferentialListNotFoundException;
import com.klem.coreapi.referential.domain.model.ReferentialEntry;
import com.klem.coreapi.referential.domain.model.ReferentialList;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Domaine {@code referential} — autonome, aucune dépendance vers un autre domaine
 * ({@code PackageBoundaryRulesTest}), lecture seule dans cette tranche (voir README.md §5 : pas
 * d'endpoint d'écriture cadré, données peuplées par migration Flyway pour ce Sprint).
 */
@Service
@Transactional(readOnly = true)
public class ReferentialService {

    private final ReferentialListRepository listRepository;
    private final ReferentialEntryRepository entryRepository;

    public ReferentialService(ReferentialListRepository listRepository, ReferentialEntryRepository entryRepository) {
        this.listRepository = listRepository;
        this.entryRepository = entryRepository;
    }

    public ReferentialList getList(String code) {
        return listRepository.findByCode(code)
                .orElseThrow(() -> new ReferentialListNotFoundException(code));
    }

    public List<ReferentialEntry> getEntries(UUID listId) {
        return entryRepository.findByListIdOrderBySortOrderAsc(listId);
    }
}
