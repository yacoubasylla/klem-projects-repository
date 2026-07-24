package com.klem.cantine.etablissement.service;

import com.klem.cantine.eleve.repository.EleveRepository;
import com.klem.cantine.etablissement.entity.Etablissement;
import com.klem.cantine.etablissement.repository.ClasseRepository;
import com.klem.cantine.etablissement.repository.EtablissementRepository;
import com.klem.cantine.etablissement.repository.NiveauRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EtablissementServiceTest {

    @Mock private EtablissementRepository etablissementRepository;
    @Mock private NiveauRepository niveauRepository;
    @Mock private ClasseRepository classeRepository;
    @Mock private EleveRepository eleveRepository;

    private EtablissementService etablissementService;

    @BeforeEach
    void setUp() {
        etablissementService = new EtablissementService(
                etablissementRepository, niveauRepository, classeRepository, eleveRepository);
    }

    private Etablissement etablissement(Long id) {
        return Etablissement.builder().id(id).nom("École B").build();
    }

    // ── supprimer (établissement) ──────────────────────────────

    @Test
    void supprimer_refuseSiNiveauxAssocies() {
        when(etablissementRepository.findById(1L)).thenReturn(Optional.of(etablissement(1L)));
        when(niveauRepository.existsByEtablissementId(1L)).thenReturn(true);

        assertThatThrownBy(() -> etablissementService.supprimer(1L))
                .isInstanceOf(IllegalStateException.class);

        verify(etablissementRepository, never()).save(any());
    }

    @Test
    void supprimer_refuseSiClassesAssociees() {
        when(etablissementRepository.findById(1L)).thenReturn(Optional.of(etablissement(1L)));
        when(niveauRepository.existsByEtablissementId(1L)).thenReturn(false);
        when(classeRepository.existsByNiveau_EtablissementId(1L)).thenReturn(true);

        assertThatThrownBy(() -> etablissementService.supprimer(1L))
                .isInstanceOf(IllegalStateException.class);

        verify(etablissementRepository, never()).save(any());
    }

    @Test
    void supprimer_refuseSiElevesAssocies() {
        when(etablissementRepository.findById(1L)).thenReturn(Optional.of(etablissement(1L)));
        when(niveauRepository.existsByEtablissementId(1L)).thenReturn(false);
        when(classeRepository.existsByNiveau_EtablissementId(1L)).thenReturn(false);
        when(eleveRepository.existsByEtablissementIdAndActifTrue(1L)).thenReturn(true);

        assertThatThrownBy(() -> etablissementService.supprimer(1L))
                .isInstanceOf(IllegalStateException.class);

        verify(etablissementRepository, never()).save(any());
    }

    @Test
    void supprimer_autoriseSiAucuneAssociation() {
        Etablissement e = etablissement(1L);
        when(etablissementRepository.findById(1L)).thenReturn(Optional.of(e));
        when(niveauRepository.existsByEtablissementId(1L)).thenReturn(false);
        when(classeRepository.existsByNiveau_EtablissementId(1L)).thenReturn(false);
        when(eleveRepository.existsByEtablissementIdAndActifTrue(1L)).thenReturn(false);

        etablissementService.supprimer(1L);

        verify(etablissementRepository).save(e);
    }

    // ── supprimerNiveau ─────────────────────────────────────────

    @Test
    void supprimerNiveau_refuseSiClassesAssociees() {
        when(niveauRepository.existsById(1L)).thenReturn(true);
        when(classeRepository.existsByNiveauId(1L)).thenReturn(true);

        assertThatThrownBy(() -> etablissementService.supprimerNiveau(1L))
                .isInstanceOf(IllegalStateException.class);

        verify(niveauRepository, never()).deleteById(any());
    }

    @Test
    void supprimerNiveau_autoriseSiAucuneClasse() {
        when(niveauRepository.existsById(1L)).thenReturn(true);
        when(classeRepository.existsByNiveauId(1L)).thenReturn(false);

        etablissementService.supprimerNiveau(1L);

        verify(niveauRepository).deleteById(1L);
    }

    // ── supprimerClasse ─────────────────────────────────────────

    @Test
    void supprimerClasse_refuseSiElevesAssocies() {
        when(classeRepository.existsById(1L)).thenReturn(true);
        when(eleveRepository.existsByClasseIdAndActifTrue(1L)).thenReturn(true);

        assertThatThrownBy(() -> etablissementService.supprimerClasse(1L))
                .isInstanceOf(IllegalStateException.class);

        verify(classeRepository, never()).deleteById(any());
    }

    @Test
    void supprimerClasse_autoriseSiAucunEleve() {
        when(classeRepository.existsById(1L)).thenReturn(true);
        when(eleveRepository.existsByClasseIdAndActifTrue(1L)).thenReturn(false);

        etablissementService.supprimerClasse(1L);

        verify(classeRepository).deleteById(1L);
    }
}
