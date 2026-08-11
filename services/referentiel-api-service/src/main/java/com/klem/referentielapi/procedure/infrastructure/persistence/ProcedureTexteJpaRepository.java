package com.klem.referentielapi.procedure.infrastructure.persistence;

import com.klem.referentielapi.procedure.application.port.ProcedureTexteRepository;
import com.klem.referentielapi.procedure.domain.model.ProcedureTexte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ProcedureTexteJpaRepository extends JpaRepository<ProcedureTexte, UUID>, ProcedureTexteRepository {

    @Override
    Optional<ProcedureTexte> findByProcedureIdAndTexteId(UUID procedureId, UUID texteId);

    @Override
    List<ProcedureTexte> findByProcedureId(UUID procedureId);
}
