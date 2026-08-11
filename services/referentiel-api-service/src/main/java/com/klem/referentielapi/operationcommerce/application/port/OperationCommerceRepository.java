package com.klem.referentielapi.operationcommerce.application.port;

import com.klem.referentielapi.operationcommerce.domain.model.OperationCommerce;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OperationCommerceRepository {

    OperationCommerce save(OperationCommerce operation);

    Optional<OperationCommerce> findById(UUID id);

    Optional<OperationCommerce> findByCode(String code);

    Page<OperationCommerce> findAll(Pageable pageable);
}
