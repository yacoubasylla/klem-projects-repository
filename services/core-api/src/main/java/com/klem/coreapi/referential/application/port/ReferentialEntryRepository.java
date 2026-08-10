package com.klem.coreapi.referential.application.port;

import com.klem.coreapi.referential.domain.model.ReferentialEntry;

import java.util.List;
import java.util.UUID;

public interface ReferentialEntryRepository {

    List<ReferentialEntry> findByListIdOrderBySortOrderAsc(UUID listId);
}
