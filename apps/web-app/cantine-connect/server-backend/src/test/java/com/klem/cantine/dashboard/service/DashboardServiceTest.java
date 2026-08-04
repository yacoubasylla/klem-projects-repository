package com.klem.cantine.dashboard.service;

import com.klem.cantine.auth.entity.Role;
import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.dashboard.dto.DashboardStatsDTO;
import com.klem.cantine.eleve.entity.StatutAcces;
import com.klem.cantine.eleve.repository.EleveRepository;
import com.klem.cantine.etablissement.repository.EtablissementRepository;
import com.klem.cantine.paiement.entity.StatutPaiement;
import com.klem.cantine.paiement.repository.TransactionPaiementRepository;
import com.klem.cantine.parent.repository.ParentRepository;
import com.klem.cantine.scan.entity.ResultatScan;
import com.klem.cantine.scan.repository.PassageRefectoireRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private EtablissementRepository etablissementRepository;
    @Mock private EleveRepository eleveRepository;
    @Mock private PassageRefectoireRepository passageRepository;
    @Mock private TransactionPaiementRepository transactionRepository;
    @Mock private ParentRepository parentRepository;
    @InjectMocks private DashboardService dashboardService;

    private Utilisateur principal(Role role) {
        return Utilisateur.builder().id(42L).role(role).build();
    }

    @Test
    void getStats_restreintAuxEnfants_quandRolePARENT() {
        Utilisateur parent = principal(Role.PARENT);
        List<Long> enfantIds = List.of(1L, 2L);
        when(parentRepository.findEnfantIdsByUtilisateurId(42L)).thenReturn(enfantIds);

        when(eleveRepository.countDistinctEtablissementByIdIn(enfantIds)).thenReturn(1L);
        when(eleveRepository.countByIdInAndActifTrue(enfantIds)).thenReturn(2L);
        when(eleveRepository.countByIdInAndStatutAccesAndActifTrue(enfantIds, StatutAcces.AUTORISE)).thenReturn(1L);
        when(eleveRepository.countByIdInAndStatutAccesAndActifTrue(enfantIds, StatutAcces.GRACE)).thenReturn(0L);
        when(eleveRepository.countByIdInAndStatutAccesAndActifTrue(enfantIds, StatutAcces.EN_ATTENTE_PAIEMENT)).thenReturn(1L);
        when(eleveRepository.countByIdInAndStatutAccesAndActifTrue(enfantIds, StatutAcces.SUSPENDU)).thenReturn(0L);
        when(passageRepository.countByDatePassageAndResultatAndEleveIdIn(any(LocalDate.class), eq(ResultatScan.ACCORDE), eq(enfantIds))).thenReturn(1L);
        when(passageRepository.countByDatePassageAndResultatAndEleveIdIn(any(LocalDate.class), eq(ResultatScan.REFUSE), eq(enfantIds))).thenReturn(0L);
        when(passageRepository.countByDateRangeGroupedForEleves(any(), any(), eq(enfantIds))).thenReturn(List.of());
        when(transactionRepository.statsAcceptesPeriodeForEleves(any(), any(), eq(enfantIds))).thenReturn(List.of());
        when(transactionRepository.countByStatutAndEleveIdIn(StatutPaiement.EN_ATTENTE, enfantIds)).thenReturn(1L);
        when(passageRepository.findTop5ByEleveIdInAndDatePassageOrderByHeurePassageDesc(eq(enfantIds), any(LocalDate.class))).thenReturn(List.of());

        DashboardStatsDTO stats = dashboardService.getStats(parent);

        assertThat(stats.totalEleves()).isEqualTo(2L);
        assertThat(stats.autorises()).isEqualTo(1L);
        assertThat(stats.nbPaiementsEnAttente()).isEqualTo(1L);

        // Aucune agrégation globale (non restreinte) ne doit être invoquée pour un PARENT.
        verify(eleveRepository, never()).countByActifTrue();
        verify(eleveRepository, never()).countByStatutAccesAndActifTrue(any());
        verify(etablissementRepository, never()).countByActifTrue();
        verify(passageRepository, never()).countByDatePassageAndResultat(any(), any());
        verify(passageRepository, never()).findTop5ByDatePassageOrderByHeurePassageDesc(any());
        verify(transactionRepository, never()).statsAcceptesPeriode(any(), any());
        verify(transactionRepository, never()).countByStatut(any());
    }

    @Test
    void getStats_retourneStatsVides_quandParentSansEnfant() {
        Utilisateur parent = principal(Role.PARENT);
        when(parentRepository.findEnfantIdsByUtilisateurId(42L)).thenReturn(List.of());

        DashboardStatsDTO stats = dashboardService.getStats(parent);

        assertThat(stats.totalEleves()).isZero();
        assertThat(stats.nbEtablissements()).isZero();
        assertThat(stats.derniersPassages()).isEmpty();
        assertThat(stats.tendance7Jours()).hasSize(7);
        verify(eleveRepository, never()).countByIdInAndActifTrue(any());
        verify(eleveRepository, never()).countByActifTrue();
    }

    @Test
    void getStats_utiliseAgregatsGlobaux_quandRoleNonParent() {
        Utilisateur admin = principal(Role.ADMIN);
        when(etablissementRepository.countByActifTrue()).thenReturn(3L);
        when(eleveRepository.countByActifTrue()).thenReturn(10L);
        when(eleveRepository.countByStatutAccesAndActifTrue(any())).thenReturn(0L);
        when(passageRepository.countByDatePassageAndResultat(any(), any())).thenReturn(0L);
        when(passageRepository.countByDateRangeGrouped(any(), any())).thenReturn(List.of());
        when(transactionRepository.statsAcceptesPeriode(any(), any())).thenReturn(List.of());
        when(transactionRepository.countByStatut(any())).thenReturn(0L);
        when(passageRepository.findTop5ByDatePassageOrderByHeurePassageDesc(any())).thenReturn(List.of());

        DashboardStatsDTO stats = dashboardService.getStats(admin);

        assertThat(stats.nbEtablissements()).isEqualTo(3L);
        assertThat(stats.totalEleves()).isEqualTo(10L);
        verify(parentRepository, never()).findEnfantIdsByUtilisateurId(anyLong());
    }
}
