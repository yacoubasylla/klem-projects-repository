package com.klem.coreapi.referential.application.port;

import com.klem.coreapi.referential.domain.model.ReferentialList;

import java.util.Optional;

public interface ReferentialListRepository {

    Optional<ReferentialList> findByCode(String code);
}
