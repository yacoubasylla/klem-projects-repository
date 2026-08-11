package com.klem.referentielapi.procedure.infrastructure.persistence;

import com.klem.referentielapi.procedure.application.port.ProcedureMetierRepository;
import com.klem.referentielapi.procedure.domain.model.ProcedureMetier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface ProcedureMetierJpaRepository extends JpaRepository<ProcedureMetier, UUID>, ProcedureMetierRepository {
}
