package com.klem.coreapi.referential.infrastructure.persistence;

import com.klem.coreapi.referential.application.port.ReferentialEntryRepository;
import com.klem.coreapi.referential.domain.model.ReferentialEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface ReferentialEntryJpaRepository extends JpaRepository<ReferentialEntry, UUID>, ReferentialEntryRepository {

    @Override
    List<ReferentialEntry> findByListIdOrderBySortOrderAsc(UUID listId);
}
