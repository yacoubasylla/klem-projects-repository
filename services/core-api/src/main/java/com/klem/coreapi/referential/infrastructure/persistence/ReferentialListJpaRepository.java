package com.klem.coreapi.referential.infrastructure.persistence;

import com.klem.coreapi.referential.application.port.ReferentialListRepository;
import com.klem.coreapi.referential.domain.model.ReferentialList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface ReferentialListJpaRepository extends JpaRepository<ReferentialList, UUID>, ReferentialListRepository {

    @Override
    Optional<ReferentialList> findByCode(String code);
}
