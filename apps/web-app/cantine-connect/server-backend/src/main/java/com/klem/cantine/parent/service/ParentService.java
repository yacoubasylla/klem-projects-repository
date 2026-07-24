package com.klem.cantine.parent.service;

import com.klem.cantine.auth.entity.Role;
import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.auth.repository.UtilisateurRepository;
import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.eleve.repository.EleveRepository;
import com.klem.cantine.parent.dto.ParentRequestDTO;
import com.klem.cantine.parent.dto.ParentResponseDTO;
import com.klem.cantine.parent.entity.Parent;
import com.klem.cantine.parent.repository.ParentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentService {

    private final ParentRepository parentRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EleveRepository eleveRepository;

    public Page<ParentResponseDTO> lister(String search, Pageable pageable) {
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        Page<Parent> resultat = searchParam != null
                ? parentRepository.findAllWithDetailsBySearch(searchParam, pageable)
                : parentRepository.findAllWithDetails(pageable);
        return resultat.map(ParentResponseDTO::from);
    }

    public ParentResponseDTO getById(Long id) {
        return ParentResponseDTO.from(trouver(id));
    }

    public ParentResponseDTO getMoi(Long utilisateurId) {
        return ParentResponseDTO.from(
            parentRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Profil parent introuvable"))
        );
    }

    @Transactional
    public ParentResponseDTO creer(ParentRequestDTO dto) {
        Utilisateur utilisateur = utilisateurRepository.findById(dto.utilisateurId())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable : " + dto.utilisateurId()));

        if (utilisateur.getRole() != Role.PARENT) {
            throw new IllegalArgumentException("L'utilisateur doit avoir le rôle PARENT");
        }
        if (parentRepository.existsByUtilisateurId(dto.utilisateurId())) {
            throw new IllegalStateException("Ce compte utilisateur est déjà lié à un profil parent");
        }

        Set<Eleve> enfants = resoudreEnfants(dto.eleveIds());
        Parent parent = Parent.builder()
                .utilisateur(utilisateur)
                .enfants(enfants)
                .build();
        return ParentResponseDTO.from(parentRepository.save(parent));
    }

    @Transactional
    public ParentResponseDTO modifierEnfants(Long id, List<Long> eleveIds) {
        Parent parent = trouver(id);
        parent.setEnfants(resoudreEnfants(eleveIds));
        return ParentResponseDTO.from(parentRepository.save(parent));
    }

    @Transactional
    public void supprimer(Long id) {
        parentRepository.delete(trouver(id));
    }

    private Parent trouver(Long id) {
        return parentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parent introuvable : " + id));
    }

    private Set<Eleve> resoudreEnfants(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        return new HashSet<>(eleveRepository.findAllById(ids));
    }
}
