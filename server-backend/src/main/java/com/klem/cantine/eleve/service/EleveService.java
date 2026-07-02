package com.klem.cantine.eleve.service;

import com.klem.cantine.actionlog.annotation.Traceable;
import com.klem.cantine.actionlog.entity.TypeAction;
import com.klem.cantine.eleve.dto.EleveRequestDTO;
import com.klem.cantine.eleve.dto.EleveResponseDTO;
import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.eleve.entity.RegimeAlimentaire;
import com.klem.cantine.eleve.entity.StatutAcces;
import com.klem.cantine.eleve.repository.EleveRepository;
import com.klem.cantine.etablissement.repository.ClasseRepository;
import com.klem.cantine.etablissement.repository.EtablissementRepository;
import com.klem.cantine.paiement.repository.TransactionPaiementRepository;
import com.klem.cantine.scan.repository.PassageRefectoireRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EleveService {

    private final EleveRepository eleveRepository;
    private final EtablissementRepository etablissementRepository;
    private final ClasseRepository classeRepository;
    private final TransactionPaiementRepository transactionPaiementRepository;
    private final PassageRefectoireRepository passageRefectoireRepository;

    public Page<EleveResponseDTO> lister(Long etablissementId, Long classeId, StatutAcces statut, String search, Pageable pageable) {
        String statutStr = statut != null ? statut.name() : null;
        return eleveRepository.findAllWithFilters(etablissementId, classeId, statutStr, search, pageable)
                .map(EleveResponseDTO::from);
    }

    public EleveResponseDTO getById(Long id) {
        return eleveRepository.findByIdActive(id)
                .map(EleveResponseDTO::from)
                .orElseThrow(() -> new EntityNotFoundException("Élève introuvable : " + id));
    }

    @Traceable(action = TypeAction.CREATE, entite = "Eleve")
    @Transactional
    public EleveResponseDTO creer(EleveRequestDTO dto) {
        if (eleveRepository.existsByMatricule(dto.matricule())) {
            throw new IllegalArgumentException("Le matricule '" + dto.matricule() + "' est déjà utilisé.");
        }

        var etablissement = etablissementRepository.findById(dto.etablissementId())
                .orElseThrow(() -> new EntityNotFoundException("Établissement introuvable : " + dto.etablissementId()));
        var classe = classeRepository.findById(dto.classeId())
                .orElseThrow(() -> new EntityNotFoundException("Classe introuvable : " + dto.classeId()));

        Eleve eleve = Eleve.builder()
                .etablissement(etablissement)
                .classe(classe)
                .matricule(dto.matricule())
                .nom(dto.nom())
                .prenom(dto.prenom())
                .dateNaissance(dto.dateNaissance())
                .photoUrl(dto.photoUrl())
                .estBoursier(dto.estBoursier() != null ? dto.estBoursier() : false)
                .regimeAlimentaire(dto.regimeAlimentaire() != null ? dto.regimeAlimentaire() : RegimeAlimentaire.STANDARD)
                .dateFinGrace(dto.dateFinGrace())
                .parentNom(dto.parentNom())
                .parentTelephone(dto.parentTelephone())
                .parentEmail(dto.parentEmail())
                .allergies(dto.allergies())
                .notesMedicales(dto.notesMedicales())
                .build();

        return EleveResponseDTO.from(eleveRepository.save(eleve));
    }

    @Traceable(action = TypeAction.UPDATE, entite = "Eleve")
    @Transactional
    public EleveResponseDTO modifier(Long id, EleveRequestDTO dto) {
        Eleve eleve = eleveRepository.findByIdActive(id)
                .orElseThrow(() -> new EntityNotFoundException("Élève introuvable : " + id));

        if (!eleve.getMatricule().equals(dto.matricule()) && eleveRepository.existsByMatricule(dto.matricule())) {
            throw new IllegalArgumentException("Le matricule '" + dto.matricule() + "' est déjà utilisé.");
        }

        var classe = classeRepository.findById(dto.classeId())
                .orElseThrow(() -> new EntityNotFoundException("Classe introuvable : " + dto.classeId()));

        eleve.setClasse(classe);
        eleve.setMatricule(dto.matricule());
        eleve.setNom(dto.nom());
        eleve.setPrenom(dto.prenom());
        eleve.setDateNaissance(dto.dateNaissance());
        eleve.setPhotoUrl(dto.photoUrl());
        eleve.setEstBoursier(dto.estBoursier() != null ? dto.estBoursier() : eleve.getEstBoursier());
        eleve.setRegimeAlimentaire(dto.regimeAlimentaire() != null ? dto.regimeAlimentaire() : eleve.getRegimeAlimentaire());
        eleve.setDateFinGrace(dto.dateFinGrace());
        eleve.setParentNom(dto.parentNom());
        eleve.setParentTelephone(dto.parentTelephone());
        eleve.setParentEmail(dto.parentEmail());
        eleve.setAllergies(dto.allergies());
        eleve.setNotesMedicales(dto.notesMedicales());

        return EleveResponseDTO.from(eleveRepository.save(eleve));
    }

    @Traceable(action = TypeAction.UPDATE, entite = "Eleve")
    @Transactional
    public EleveResponseDTO changerStatut(Long id, StatutAcces nouveauStatut) {
        Eleve eleve = eleveRepository.findByIdActive(id)
                .orElseThrow(() -> new EntityNotFoundException("Élève introuvable : " + id));
        eleve.setStatutAcces(nouveauStatut);
        return EleveResponseDTO.from(eleveRepository.save(eleve));
    }

    @Traceable(action = TypeAction.DELETE, entite = "Eleve")
    @Transactional
    public void supprimer(Long id) {
        Eleve eleve = eleveRepository.findByIdActive(id)
                .orElseThrow(() -> new EntityNotFoundException("Élève introuvable : " + id));
        eleve.setActif(false);
        eleveRepository.save(eleve);
    }

    @Traceable(action = TypeAction.DELETE, entite = "Eleve")
    @Transactional
    public void supprimerDefinitivement(Long id) {
        Eleve eleve = eleveRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Élève introuvable : " + id));
        if (transactionPaiementRepository.existsByEleveId(id)) {
            throw new IllegalStateException("Impossible de supprimer : cet élève a des paiements associés.");
        }
        if (passageRefectoireRepository.existsByEleveId(id)) {
            throw new IllegalStateException("Impossible de supprimer : cet élève a des passages au réfectoire associés.");
        }
        eleveRepository.delete(eleve);
    }
}
