package com.klem.referentielapi.procedure.application.service;

import com.klem.referentielapi.procedure.application.port.ProcedureMetierRepository;
import com.klem.referentielapi.procedure.application.port.ProcedureTexteRepository;
import com.klem.referentielapi.procedure.domain.exception.ProcedureMetierNotFoundException;
import com.klem.referentielapi.procedure.domain.exception.TexteAlreadyAssociatedException;
import com.klem.referentielapi.procedure.domain.exception.UnknownTexteReglementaireException;
import com.klem.referentielapi.procedure.domain.model.ProcedureMetier;
import com.klem.referentielapi.procedure.domain.model.ProcedureTexte;
import com.klem.referentielapi.shared.domain.StatutPublication;
import com.klem.referentielapi.textereglementaire.application.service.TexteReglementaireService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Cas d'usage du domaine {@code procedure} — transactions définies ici, jamais dans le controller.
 * <p>
 * Dépend de {@link TexteReglementaireService#exists(UUID)} — lecture peer-à-peer étroite vers le
 * domaine {@code textereglementaire}, jamais de son {@code domain.model}/{@code domain.exception}
 * (même motif que {@code TenantService.tenantExists} sur {@code core-api}, voir
 * {@code PackageBoundaryRulesTest}).
 */
@Service
@Transactional(readOnly = true)
public class ProcedureMetierService {

    private final ProcedureMetierRepository procedureRepository;
    private final ProcedureTexteRepository procedureTexteRepository;
    private final TexteReglementaireService texteReglementaireService;

    public ProcedureMetierService(ProcedureMetierRepository procedureRepository,
                                   ProcedureTexteRepository procedureTexteRepository,
                                   TexteReglementaireService texteReglementaireService) {
        this.procedureRepository = procedureRepository;
        this.procedureTexteRepository = procedureTexteRepository;
        this.texteReglementaireService = texteReglementaireService;
    }

    @Transactional
    public ProcedureMetier propose(String nom, String code, String description, String acteurs, String createdBy) {
        return procedureRepository.save(ProcedureMetier.propose(nom, code, description, acteurs, createdBy));
    }

    public ProcedureMetier get(UUID id) {
        return procedureRepository.findById(id)
                .orElseThrow(() -> new ProcedureMetierNotFoundException(id));
    }

    public Page<ProcedureMetier> list(Pageable pageable) {
        return procedureRepository.findAll(pageable);
    }

    @Transactional
    public ProcedureMetier changeStatus(UUID id, StatutPublication newStatus, String actorSubject) {
        ProcedureMetier procedure = get(id);
        procedure.changeStatus(newStatus, actorSubject);
        return procedureRepository.save(procedure);
    }

    @Transactional
    public void associateTexte(UUID procedureId, UUID texteId) {
        get(procedureId); // valide l'existence, lève ProcedureMetierNotFoundException sinon
        if (!texteReglementaireService.exists(texteId)) {
            throw new UnknownTexteReglementaireException(texteId);
        }
        if (procedureTexteRepository.findByProcedureIdAndTexteId(procedureId, texteId).isPresent()) {
            throw new TexteAlreadyAssociatedException(procedureId, texteId);
        }
        procedureTexteRepository.save(ProcedureTexte.associate(procedureId, texteId));
    }

    @Transactional
    public void dissociateTexte(UUID procedureId, UUID texteId) {
        ProcedureTexte association = procedureTexteRepository.findByProcedureIdAndTexteId(procedureId, texteId)
                .orElseThrow(() -> new UnknownTexteReglementaireException(texteId));
        procedureTexteRepository.delete(association);
    }

    public List<UUID> listTexteIds(UUID procedureId) {
        get(procedureId); // valide l'existence
        return procedureTexteRepository.findByProcedureId(procedureId).stream()
                .map(ProcedureTexte::getTexteId)
                .toList();
    }

    /**
     * Vérification d'existence exposée au domaine {@code operationcommerce} (FK
     * {@code procedure_id}) — même motif de lecture peer-à-peer étroite que {@code #associateTexte}
     * vers {@code textereglementaire}.
     */
    public boolean exists(UUID id) {
        return procedureRepository.existsById(id);
    }
}
