package com.klem.referentielapi.procedure.application.port;

import com.klem.referentielapi.procedure.domain.model.ProcedureTexte;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProcedureTexteRepository {

    ProcedureTexte save(ProcedureTexte association);

    Optional<ProcedureTexte> findByProcedureIdAndTexteId(UUID procedureId, UUID texteId);

    List<ProcedureTexte> findByProcedureId(UUID procedureId);

    void delete(ProcedureTexte association);
}
