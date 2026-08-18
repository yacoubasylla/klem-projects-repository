package com.klem.cantine.eleve.service;

import com.klem.cantine.auth.entity.Role;
import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.eleve.dto.EleveRequestDTO;
import com.klem.cantine.eleve.dto.EleveResponseDTO;
import com.klem.cantine.eleve.dto.ModifierEnfantRequestDTO;
import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.eleve.entity.StatutAcces;
import com.klem.cantine.eleve.repository.EleveRepository;
import com.klem.cantine.etablissement.entity.Classe;
import com.klem.cantine.etablissement.entity.Etablissement;
import com.klem.cantine.etablissement.repository.ClasseRepository;
import com.klem.cantine.etablissement.repository.EtablissementRepository;
import com.klem.cantine.paiement.repository.TransactionPaiementRepository;
import com.klem.cantine.parent.entity.Parent;
import com.klem.cantine.parent.repository.ParentRepository;
import com.klem.cantine.scan.repository.PassageRefectoireRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EleveServiceTest {

    @Mock private EleveRepository eleveRepository;
    @Mock private EtablissementRepository etablissementRepository;
    @Mock private ClasseRepository classeRepository;
    @Mock private TransactionPaiementRepository transactionPaiementRepository;
    @Mock private PassageRefectoireRepository passageRefectoireRepository;
    @Mock private ParentRepository parentRepository;
    @Mock private MatriculeGenerator matriculeGenerator;
    @InjectMocks private EleveService eleveService;

    // ── Helpers ───────────────────────────────────────────────

    private EleveRequestDTO dto(Long etabId, Long classeId) {
        return new EleveRequestDTO(
                etabId, classeId,
                "Traoré", "Aminata", null, null,
                false, null, null,
                "Parent Traoré", "0700000001",
                null, null, null, null, null);
    }

    private Eleve eleveComplet(Long id, String matricule, StatutAcces statut) {
        Etablissement etab = Etablissement.builder().id(1L).nom("Lycée Moderne").build();
        Classe classe = Classe.builder().id(1L).libelle("6ème A").anneeScolaire("2025-2026").build();
        return Eleve.builder()
                .id(id)
                .etablissement(etab)
                .classe(classe)
                .matricule(matricule)
                .nom("Traoré")
                .prenom("Aminata")
                .qrCodeToken(UUID.randomUUID())
                .statutAcces(statut)
                .parentNom("Parent Traoré")
                .parentTelephone("0700000001")
                .build();
    }

    // ── Tests creer ───────────────────────────────────────────

    @Test
    void creer_creeEleve_avecMatriculeGenereAutomatiquement() {
        Etablissement etab = Etablissement.builder().id(1L).nom("Lycée Moderne").build();
        Classe classe = Classe.builder().id(1L).libelle("6ème A").anneeScolaire("2025-2026").build();
        Eleve saved = eleveComplet(1L, "E20260001", StatutAcces.EN_ATTENTE_PAIEMENT);

        when(matriculeGenerator.genererMatricule()).thenReturn("E20260001");
        when(etablissementRepository.findById(1L)).thenReturn(Optional.of(etab));
        when(classeRepository.findById(1L)).thenReturn(Optional.of(classe));
        when(eleveRepository.save(any())).thenReturn(saved);

        EleveResponseDTO result = eleveService.creer(dto(1L, 1L));

        assertThat(result.matricule()).isEqualTo("E20260001");
        assertThat(result.statutAcces()).isEqualTo(StatutAcces.EN_ATTENTE_PAIEMENT);
    }

    @Test
    void creer_leveException_quandEtablissementIntrouvable() {
        when(etablissementRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eleveService.creer(dto(99L, 1L)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── Tests supprimer ───────────────────────────────────────

    @Test
    void supprimer_softDelete_metActifAFalse() {
        Eleve eleve = eleveComplet(5L, "MAT-005", StatutAcces.AUTORISE);
        when(eleveRepository.findByIdActive(5L)).thenReturn(Optional.of(eleve));
        when(eleveRepository.save(any())).thenReturn(eleve);

        eleveService.supprimer(5L);

        verify(eleveRepository).save(argThat(e -> Boolean.FALSE.equals(e.getActif())));
    }

    // ── Tests supprimerDefinitivement ─────────────────────────

    @Test
    void supprimerDefinitivement_refuseSiPaiementsAssocies() {
        Eleve eleve = eleveComplet(7L, "MAT-007", StatutAcces.AUTORISE);
        when(eleveRepository.findById(7L)).thenReturn(Optional.of(eleve));
        when(transactionPaiementRepository.existsByEleveId(7L)).thenReturn(true);

        assertThatThrownBy(() -> eleveService.supprimerDefinitivement(7L))
                .isInstanceOf(IllegalStateException.class);

        verify(eleveRepository, never()).delete(any());
    }

    @Test
    void supprimerDefinitivement_refuseSiPassagesAssocies() {
        Eleve eleve = eleveComplet(8L, "MAT-008", StatutAcces.AUTORISE);
        when(eleveRepository.findById(8L)).thenReturn(Optional.of(eleve));
        when(transactionPaiementRepository.existsByEleveId(8L)).thenReturn(false);
        when(passageRefectoireRepository.existsByEleveId(8L)).thenReturn(true);

        assertThatThrownBy(() -> eleveService.supprimerDefinitivement(8L))
                .isInstanceOf(IllegalStateException.class);

        verify(eleveRepository, never()).delete(any());
    }

    @Test
    void supprimerDefinitivement_autoriseSiAucunPaiementNiPassage() {
        Eleve eleve = eleveComplet(9L, "MAT-009", StatutAcces.AUTORISE);
        when(eleveRepository.findById(9L)).thenReturn(Optional.of(eleve));
        when(transactionPaiementRepository.existsByEleveId(9L)).thenReturn(false);
        when(passageRefectoireRepository.existsByEleveId(9L)).thenReturn(false);

        eleveService.supprimerDefinitivement(9L);

        verify(eleveRepository).delete(eleve);
    }

    // ── Tests changerStatut ───────────────────────────────────

    @Test
    void changerStatut_metAJourLeStatut() {
        Eleve eleve = eleveComplet(6L, "MAT-006", StatutAcces.EN_ATTENTE_PAIEMENT);
        when(eleveRepository.findByIdActive(6L)).thenReturn(Optional.of(eleve));
        when(eleveRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EleveResponseDTO result = eleveService.changerStatut(6L, StatutAcces.AUTORISE);

        assertThat(result.statutAcces()).isEqualTo(StatutAcces.AUTORISE);
    }

    // ── Tests modifierViaParent / desactiverViaParent ──────────

    private Utilisateur parentPrincipal() {
        return Utilisateur.builder().id(42L).nom("Kone").prenom("Awa").role(Role.PARENT).build();
    }

    private ModifierEnfantRequestDTO dtoModification() {
        return new ModifierEnfantRequestDTO(1L, 1L, "Traoré", "Aminata", null, null, "Abidjan", "Cocody", null);
    }

    @Test
    void modifierViaParent_enfantDuParent_estModifie() {
        Eleve eleve = eleveComplet(10L, "E20260010", StatutAcces.EN_ATTENTE_PAIEMENT);
        Etablissement etab = eleve.getEtablissement();
        Classe classe = eleve.getClasse();
        Parent parent = Parent.builder().id(1L).enfants(new HashSet<>(Set.of(eleve))).build();

        when(eleveRepository.findByIdActive(10L)).thenReturn(Optional.of(eleve));
        when(parentRepository.findByUtilisateurId(42L)).thenReturn(Optional.of(parent));
        when(etablissementRepository.findById(1L)).thenReturn(Optional.of(etab));
        when(classeRepository.findById(1L)).thenReturn(Optional.of(classe));
        when(eleveRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EleveResponseDTO result = eleveService.modifierViaParent(parentPrincipal(), 10L, dtoModification());

        assertThat(result.ville()).isEqualTo("Abidjan");
    }

    @Test
    void modifierViaParent_enfantDunAutreParent_refuseAvecAccessDenied() {
        Eleve eleve = eleveComplet(11L, "E20260011", StatutAcces.EN_ATTENTE_PAIEMENT);
        Parent parent = Parent.builder().id(2L).enfants(new HashSet<>()).build();

        when(eleveRepository.findByIdActive(11L)).thenReturn(Optional.of(eleve));
        when(parentRepository.findByUtilisateurId(42L)).thenReturn(Optional.of(parent));

        assertThatThrownBy(() -> eleveService.modifierViaParent(parentPrincipal(), 11L, dtoModification()))
                .isInstanceOf(AccessDeniedException.class);

        verify(eleveRepository, never()).save(any());
    }

    @Test
    void desactiverViaParent_enfantDuParent_metActifAFalse() {
        Eleve eleve = eleveComplet(12L, "E20260012", StatutAcces.AUTORISE);
        Parent parent = Parent.builder().id(1L).enfants(new HashSet<>(Set.of(eleve))).build();

        when(eleveRepository.findByIdActive(12L)).thenReturn(Optional.of(eleve));
        when(parentRepository.findByUtilisateurId(42L)).thenReturn(Optional.of(parent));
        when(eleveRepository.save(any())).thenReturn(eleve);

        eleveService.desactiverViaParent(parentPrincipal(), 12L);

        verify(eleveRepository).save(argThat(e -> Boolean.FALSE.equals(e.getActif())));
    }

    @Test
    void desactiverViaParent_enfantDunAutreParent_refuseAvecAccessDenied() {
        Eleve eleve = eleveComplet(13L, "E20260013", StatutAcces.AUTORISE);
        Parent parent = Parent.builder().id(2L).enfants(new HashSet<>()).build();

        when(eleveRepository.findByIdActive(13L)).thenReturn(Optional.of(eleve));
        when(parentRepository.findByUtilisateurId(42L)).thenReturn(Optional.of(parent));

        assertThatThrownBy(() -> eleveService.desactiverViaParent(parentPrincipal(), 13L))
                .isInstanceOf(AccessDeniedException.class);

        verify(eleveRepository, never()).save(any());
    }
}
