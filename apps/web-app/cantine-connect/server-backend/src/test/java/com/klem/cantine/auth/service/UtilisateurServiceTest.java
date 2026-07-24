package com.klem.cantine.auth.service;

import com.klem.cantine.auth.dto.ChangerRoleRequestDTO;
import com.klem.cantine.auth.dto.CreerUtilisateurRequestDTO;
import com.klem.cantine.auth.dto.UtilisateurResponseDTO;
import com.klem.cantine.auth.entity.Role;
import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.auth.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UtilisateurService utilisateurService;

    // ── Helper ────────────────────────────────────────────────

    private Utilisateur utilisateur(Long id, Role role, boolean actif) {
        return Utilisateur.builder()
                .id(id)
                .nom("Sylla")
                .prenom("Yacouba")
                .email("user" + id + "@cantine.test")
                .telephone("070000000" + id)
                .motDePasse("encoded")
                .role(role)
                .actif(actif)
                .build();
    }

    // ── Tests creer ───────────────────────────────────────────

    @Test
    void creer_creeCompte_quandEmailDisponible() {
        CreerUtilisateurRequestDTO dto = new CreerUtilisateurRequestDTO(
                "Koné", "Awa", "awa@cantine.test", "0700000000", "Mdp12345!", Role.GESTIONNAIRE);
        Utilisateur saved = utilisateur(1L, Role.GESTIONNAIRE, true);

        when(utilisateurRepository.existsByEmail("awa@cantine.test")).thenReturn(false);
        when(utilisateurRepository.existsByTelephone("0700000000")).thenReturn(false);
        when(passwordEncoder.encode("Mdp12345!")).thenReturn("encoded");
        when(utilisateurRepository.save(any())).thenReturn(saved);

        UtilisateurResponseDTO result = utilisateurService.creer(dto);

        assertThat(result.role()).isEqualTo(Role.GESTIONNAIRE);
        assertThat(result.actif()).isTrue();
        verify(passwordEncoder).encode("Mdp12345!");
    }

    @Test
    void creer_leveException_quandEmailDejaUtilise() {
        CreerUtilisateurRequestDTO dto = new CreerUtilisateurRequestDTO(
                "Koné", "Awa", "awa@cantine.test", "0700000000", "Mdp12345!", Role.GESTIONNAIRE);
        when(utilisateurRepository.existsByEmail("awa@cantine.test")).thenReturn(true);

        assertThatThrownBy(() -> utilisateurService.creer(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("awa@cantine.test");
    }

    @Test
    void creer_leveException_quandTelephoneDejaUtilise() {
        CreerUtilisateurRequestDTO dto = new CreerUtilisateurRequestDTO(
                "Koné", "Awa", "awa@cantine.test", "0700000000", "Mdp12345!", Role.GESTIONNAIRE);
        when(utilisateurRepository.existsByEmail("awa@cantine.test")).thenReturn(false);
        when(utilisateurRepository.existsByTelephone("0700000000")).thenReturn(true);

        assertThatThrownBy(() -> utilisateurService.creer(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("0700000000");
    }

    // ── Tests desactiver ─────────────────────────────────────

    @Test
    void desactiver_desactiveCompte_quandPasSeulAdmin() {
        Utilisateur admin = utilisateur(1L, Role.ADMIN, true);
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(utilisateurRepository.countByRoleAndActifTrue(Role.ADMIN)).thenReturn(2L);
        when(utilisateurRepository.save(any())).thenReturn(admin);

        utilisateurService.desactiver(1L);

        verify(utilisateurRepository).save(argThat(u -> Boolean.FALSE.equals(u.getActif())));
    }

    @Test
    void desactiver_leveException_quandDernierAdmin() {
        Utilisateur admin = utilisateur(1L, Role.ADMIN, true);
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(utilisateurRepository.countByRoleAndActifTrue(Role.ADMIN)).thenReturn(1L);

        assertThatThrownBy(() -> utilisateurService.desactiver(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("dernier compte ADMIN");
    }

    @Test
    void desactiver_leveException_quandDejaInactif() {
        Utilisateur inactif = utilisateur(2L, Role.GESTIONNAIRE, false);
        when(utilisateurRepository.findById(2L)).thenReturn(Optional.of(inactif));

        assertThatThrownBy(() -> utilisateurService.desactiver(2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("déjà désactivé");
    }

    // ── Tests changerRole ────────────────────────────────────

    @Test
    void changerRole_metAJourLeRole() {
        Utilisateur gestionnaire = utilisateur(3L, Role.GESTIONNAIRE, true);
        when(utilisateurRepository.findById(3L)).thenReturn(Optional.of(gestionnaire));
        when(utilisateurRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UtilisateurResponseDTO result = utilisateurService.changerRole(3L,
                new ChangerRoleRequestDTO(Role.CAISSIER));

        assertThat(result.role()).isEqualTo(Role.CAISSIER);
    }

    // ── Tests reactiver ──────────────────────────────────────

    @Test
    void reactiver_reactiveLeCompte() {
        Utilisateur inactif = utilisateur(4L, Role.GESTIONNAIRE, false);
        when(utilisateurRepository.findById(4L)).thenReturn(Optional.of(inactif));
        when(utilisateurRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UtilisateurResponseDTO result = utilisateurService.reactiver(4L);

        assertThat(result.actif()).isTrue();
    }

    @Test
    void getById_leveException_quandIntrouvable() {
        when(utilisateurRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> utilisateurService.getById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }
}
