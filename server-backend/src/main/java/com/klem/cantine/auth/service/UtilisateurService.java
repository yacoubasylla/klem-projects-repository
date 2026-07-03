package com.klem.cantine.auth.service;

import com.klem.cantine.auth.dto.ChangerRoleRequestDTO;
import com.klem.cantine.auth.dto.CreerUtilisateurRequestDTO;
import com.klem.cantine.auth.dto.ModifierUtilisateurRequestDTO;
import com.klem.cantine.auth.dto.UtilisateurResponseDTO;
import com.klem.cantine.auth.entity.Role;
import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.auth.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UtilisateurResponseDTO> lister(Role role, Boolean actif, LocalDate dateDebut, LocalDate dateFin,
                                                String search, Pageable pageable) {
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        String roleParam = role != null ? role.name() : null;
        return utilisateurRepository.findAllFiltered(roleParam, actif, dateDebut, dateFin, searchParam, pageable)
                .map(UtilisateurResponseDTO::from);
    }

    public UtilisateurResponseDTO getById(Long id) {
        return utilisateurRepository.findById(id)
                .map(UtilisateurResponseDTO::from)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable : " + id));
    }

    @Transactional
    public UtilisateurResponseDTO creer(CreerUtilisateurRequestDTO dto) {
        if (utilisateurRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Un compte existe déjà avec l'email : " + dto.email());
        }
        if (utilisateurRepository.existsByTelephone(dto.telephone())) {
            throw new IllegalArgumentException("Un compte existe déjà avec le numéro : " + dto.telephone());
        }
        Utilisateur u = Utilisateur.builder()
                .nom(dto.nom())
                .prenom(dto.prenom())
                .email(dto.email())
                .telephone(dto.telephone())
                .motDePasse(passwordEncoder.encode(dto.motDePasse()))
                .role(dto.role())
                .build();
        return UtilisateurResponseDTO.from(utilisateurRepository.save(u));
    }

    @Transactional
    public UtilisateurResponseDTO modifier(Long id, ModifierUtilisateurRequestDTO dto) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable : " + id));
        // Email uniqueness check (seulement si l'email change)
        if (!u.getEmail().equalsIgnoreCase(dto.email())
                && utilisateurRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Un compte existe déjà avec l'email : " + dto.email());
        }
        // Téléphone uniqueness check (seulement si le numéro change)
        if (!u.getTelephone().equals(dto.telephone())
                && utilisateurRepository.existsByTelephone(dto.telephone())) {
            throw new IllegalArgumentException("Un compte existe déjà avec le numéro : " + dto.telephone());
        }
        u.setNom(dto.nom());
        u.setPrenom(dto.prenom());
        u.setEmail(dto.email());
        u.setTelephone(dto.telephone());
        if (dto.nouveauMotDePasse() != null && !dto.nouveauMotDePasse().isBlank()) {
            if (dto.nouveauMotDePasse().length() < 8) {
                throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caractères.");
            }
            u.setMotDePasse(passwordEncoder.encode(dto.nouveauMotDePasse()));
        }
        return UtilisateurResponseDTO.from(utilisateurRepository.save(u));
    }

    @Transactional
    public UtilisateurResponseDTO changerRole(Long id, ChangerRoleRequestDTO dto) {
        Utilisateur u = trouverActif(id);
        u.setRole(dto.role());
        return UtilisateurResponseDTO.from(utilisateurRepository.save(u));
    }

    @Transactional
    public void desactiver(Long id) {
        Utilisateur u = trouverActif(id);
        // Interdire la désactivation du dernier ADMIN
        if (u.getRole() == Role.ADMIN) {
            long nbAdminsActifs = utilisateurRepository.countByRoleAndActifTrue(Role.ADMIN);
            if (nbAdminsActifs <= 1) {
                throw new IllegalStateException(
                        "Impossible de désactiver le dernier compte ADMIN du système.");
            }
        }
        u.setActif(false);
        utilisateurRepository.save(u);
    }

    @Transactional
    public UtilisateurResponseDTO reactiver(Long id) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable : " + id));
        u.setActif(true);
        return UtilisateurResponseDTO.from(utilisateurRepository.save(u));
    }

    @Transactional
    public void supprimerDefinitivement(Long id) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable : " + id));
        if (u.getRole() == Role.ADMIN) {
            long nbAdmins = utilisateurRepository.countByRoleAndActifTrue(Role.ADMIN);
            if (nbAdmins <= 1) {
                throw new IllegalStateException(
                        "Impossible de supprimer le dernier compte ADMIN du système.");
            }
        }
        utilisateurRepository.delete(u);
    }

    private Utilisateur trouverActif(Long id) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable : " + id));
        if (!u.getActif()) {
            throw new IllegalStateException("Utilisateur déjà désactivé : " + id);
        }
        return u;
    }
}
