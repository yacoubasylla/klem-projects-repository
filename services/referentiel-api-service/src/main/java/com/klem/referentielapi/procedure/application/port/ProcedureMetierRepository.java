package com.klem.referentielapi.procedure.application.port;

import com.klem.referentielapi.procedure.domain.model.ProcedureMetier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProcedureMetierRepository {

    ProcedureMetier save(ProcedureMetier procedure);

    Optional<ProcedureMetier> findById(UUID id);

    Page<ProcedureMetier> findAll(Pageable pageable);

    boolean existsById(UUID id);
}
