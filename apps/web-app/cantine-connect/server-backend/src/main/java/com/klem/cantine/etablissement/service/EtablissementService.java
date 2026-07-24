package com.klem.cantine.etablissement.service;

import com.klem.cantine.actionlog.annotation.Traceable;
import com.klem.cantine.actionlog.entity.TypeAction;
import com.klem.cantine.eleve.repository.EleveRepository;
import com.klem.cantine.etablissement.dto.*;
import com.klem.cantine.etablissement.entity.Classe;
import com.klem.cantine.etablissement.entity.Etablissement;
import com.klem.cantine.etablissement.entity.Niveau;
import com.klem.cantine.etablissement.repository.ClasseRepository;
import com.klem.cantine.etablissement.repository.EtablissementRepository;
import com.klem.cantine.etablissement.repository.NiveauRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EtablissementService {

    private final EtablissementRepository etablissementRepository;
    private final NiveauRepository niveauRepository;
    private final ClasseRepository classeRepository;
    private final EleveRepository eleveRepository;

    @Traceable(action = TypeAction.CREATE, entite = "Etablissement")
    @Transactional
    public EtablissementResponseDTO creer(EtablissementRequestDTO dto) {
        Etablissement e = Etablissement.builder()
                .nom(dto.nom())
                .adresse(dto.adresse())
                .ville(dto.ville() != null ? dto.ville() : "Abidjan")
                .telephone(dto.telephone())
                .build();
        return EtablissementResponseDTO.from(etablissementRepository.save(e));
    }

    public List<EtablissementResponseDTO> listerActifs() {
        return etablissementRepository.findByActifTrue()
                .stream()
                .map(EtablissementResponseDTO::from)
                .toList();
    }

    public EtablissementResponseDTO getById(Long id) {
        return etablissementRepository.findById(id)
                .filter(e -> e.getActif())
                .map(EtablissementResponseDTO::from)
                .orElseThrow(() -> new EntityNotFoundException("Établissement introuvable : " + id));
    }

    public List<ClasseResponseDTO> getClassesByEtablissement(Long etablissementId, String anneeScolaire) {
        if (!etablissementRepository.existsById(etablissementId)) {
            throw new EntityNotFoundException("Établissement introuvable : " + etablissementId);
        }
        return classeRepository.findByEtablissementAndAnnee(etablissementId, anneeScolaire)
                .stream()
                .map(ClasseResponseDTO::from)
                .toList();
    }

    public List<NiveauResponseDTO> getNiveauxAvecClasses(Long etablissementId) {
        if (!etablissementRepository.existsById(etablissementId)) {
            throw new EntityNotFoundException("Établissement introuvable : " + etablissementId);
        }
        return niveauRepository.findByEtablissementIdOrderByOrdre(etablissementId)
                .stream()
                .map(NiveauResponseDTO::from)
                .toList();
    }

    @Traceable(action = TypeAction.UPDATE, entite = "Etablissement")
    @Transactional
    public EtablissementResponseDTO modifier(Long id, EtablissementRequestDTO dto) {
        Etablissement e = etablissementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Établissement introuvable : " + id));
        if (dto.nom() != null && !dto.nom().isBlank()) e.setNom(dto.nom());
        if (dto.adresse() != null) e.setAdresse(dto.adresse());
        if (dto.ville() != null && !dto.ville().isBlank()) e.setVille(dto.ville());
        if (dto.telephone() != null) e.setTelephone(dto.telephone());
        return EtablissementResponseDTO.from(etablissementRepository.save(e));
    }

    @Traceable(action = TypeAction.DELETE, entite = "Etablissement")
    @Transactional
    public void supprimer(Long id) {
        Etablissement e = etablissementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Établissement introuvable : " + id));
        if (niveauRepository.existsByEtablissementId(id)) {
            throw new IllegalStateException("Impossible de supprimer : cet établissement a des niveaux associés.");
        }
        if (classeRepository.existsByNiveau_EtablissementId(id)) {
            throw new IllegalStateException("Impossible de supprimer : cet établissement a des classes associées.");
        }
        if (eleveRepository.existsByEtablissementIdAndActifTrue(id)) {
            throw new IllegalStateException("Impossible de supprimer : cet établissement a des élèves associés.");
        }
        e.setActif(false);
        etablissementRepository.save(e);
    }

    @Traceable(action = TypeAction.CREATE, entite = "Niveau")
    @Transactional
    public NiveauResponseDTO creerNiveau(Long etablissementId, NiveauRequestDTO dto) {
        Etablissement etab = etablissementRepository.findById(etablissementId)
                .orElseThrow(() -> new EntityNotFoundException("Établissement introuvable : " + etablissementId));
        Niveau niveau = Niveau.builder()
                .etablissement(etab)
                .libelle(dto.libelle())
                .ordre(dto.ordre() != null ? dto.ordre() : 0)
                .build();
        return NiveauResponseDTO.from(niveauRepository.save(niveau));
    }

    @Traceable(action = TypeAction.DELETE, entite = "Niveau")
    @Transactional
    public void supprimerNiveau(Long niveauId) {
        if (!niveauRepository.existsById(niveauId)) {
            throw new EntityNotFoundException("Niveau introuvable : " + niveauId);
        }
        if (classeRepository.existsByNiveauId(niveauId)) {
            throw new IllegalStateException("Impossible de supprimer : ce niveau a des classes associées.");
        }
        niveauRepository.deleteById(niveauId);
    }

    @Traceable(action = TypeAction.UPDATE, entite = "Niveau")
    @Transactional
    public NiveauResponseDTO modifierNiveau(Long niveauId, NiveauRequestDTO dto) {
        Niveau niveau = niveauRepository.findById(niveauId)
                .orElseThrow(() -> new EntityNotFoundException("Niveau introuvable : " + niveauId));
        if (dto.libelle() != null && !dto.libelle().isBlank()) niveau.setLibelle(dto.libelle());
        if (dto.ordre() != null) niveau.setOrdre(dto.ordre());
        return NiveauResponseDTO.from(niveauRepository.save(niveau));
    }

    @Traceable(action = TypeAction.UPDATE, entite = "Classe")
    @Transactional
    public ClasseResponseDTO modifierClasse(Long classeId, ClasseRequestDTO dto) {
        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new EntityNotFoundException("Classe introuvable : " + classeId));
        if (dto.libelle() != null && !dto.libelle().isBlank()) classe.setLibelle(dto.libelle());
        if (dto.anneeScolaire() != null && !dto.anneeScolaire().isBlank()) classe.setAnneeScolaire(dto.anneeScolaire());
        return ClasseResponseDTO.from(classeRepository.save(classe));
    }

    @Traceable(action = TypeAction.DELETE, entite = "Classe")
    @Transactional
    public void supprimerClasse(Long classeId) {
        if (!classeRepository.existsById(classeId)) {
            throw new EntityNotFoundException("Classe introuvable : " + classeId);
        }
        if (eleveRepository.existsByClasseIdAndActifTrue(classeId)) {
            throw new IllegalStateException("Impossible de supprimer : cette classe a des élèves associés.");
        }
        classeRepository.deleteById(classeId);
    }

    @Traceable(action = TypeAction.CREATE, entite = "Classe")
    @Transactional
    public ClasseResponseDTO creerClasse(Long niveauId, ClasseRequestDTO dto) {
        Niveau niveau = niveauRepository.findById(niveauId)
                .orElseThrow(() -> new EntityNotFoundException("Niveau introuvable : " + niveauId));
        String annee = (dto.anneeScolaire() != null && !dto.anneeScolaire().isBlank())
                ? dto.anneeScolaire() : "2025-2026";
        Classe classe = Classe.builder()
                .niveau(niveau)
                .libelle(dto.libelle())
                .anneeScolaire(annee)
                .build();
        return ClasseResponseDTO.from(classeRepository.save(classe));
    }
}
