package com.klem.referentielapi.operationcommerce.infrastructure.persistence;

import com.klem.referentielapi.operationcommerce.application.port.OperationCommerceRepository;
import com.klem.referentielapi.operationcommerce.domain.model.OperationCommerce;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface OperationCommerceJpaRepository extends JpaRepository<OperationCommerce, UUID>, OperationCommerceRepository {

    @Override
    Optional<OperationCommerce> findByCode(String code);
}
