package com.klem.cantine.scan.service;

import com.klem.cantine.auth.entity.Role;
import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.eleve.entity.StatutAcces;
import com.klem.cantine.eleve.repository.EleveRepository;
import com.klem.cantine.etablissement.entity.Classe;
import com.klem.cantine.etablissement.entity.Etablissement;
import com.klem.cantine.notification.NotificationService;
import com.klem.cantine.parametrage.service.ConfigurationService;
import com.klem.cantine.parent.repository.ParentRepository;
import com.klem.cantine.scan.dto.PassageResponseDTO;
import com.klem.cantine.scan.dto.ScanResultDTO;
import com.klem.cantine.scan.entity.MotifRefus;
import com.klem.cantine.scan.entity.PassageRefectoire;
import com.klem.cantine.scan.entity.ResultatScan;
import com.klem.cantine.scan.repository.PassageRefectoireRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScanServiceTest {

    @Mock private PassageRefectoireRepository passageRepository;
    @Mock private EleveRepository eleveRepository;
    @Mock private ParentRepository parentRepository;
    @Mock private NotificationService notificationService;
    @Mock private ConfigurationService configurationService;
    @InjectMocks private ScanService scanService;

    private Utilisateur principal(Role role) {
        return Utilisateur.builder().id(42L).role(role).build();
    }

    // ── Helpers ───────────────────────────────────────────────

    private Eleve eleve(Long id, StatutAcces statut) {
        Etablissement etab = Etablissement.builder().id(1L).nom("École Primaire A").build();
        Classe classe = Classe.builder().id(1L).libelle("CM2 A").anneeScolaire("2025-2026").build();
        return Eleve.builder()
                .id(id)
                .etablissement(etab)
                .classe(classe)
                .matricule("MAT-00" + id)
                .nom("Koné")
                .prenom("Fatou")
                .qrCodeToken(UUID.randomUUID())
                .statutAcces(statut)
                .parentNom("Koné Senior")
                .parentTelephone("0700000000")
                .build();
    }

    private PassageRefectoire passage(Eleve e, ResultatScan resultat, MotifRefus motif) {
        return PassageRefectoire.builder()
                .id(1L)
                .eleve(e)
                .qrCodeToken(e.getQrCodeToken())
                .datePassage(LocalDate.now())
                .heurePassage(LocalDateTime.now())
                .resultat(resultat)
                .motifRefus(motif)
                .build();
    }

    // ── Tests ─────────────────────────────────────────────────

    @Test
    void scanner_accorde_quandStatutAutorise() {
        Eleve e = eleve(1L, StatutAcces.AUTORISE);
        when(eleveRepository.findByQrCodeTokenAndActifTrue(e.getQrCodeToken()))
                .thenReturn(Optional.of(e));
        when(passageRepository.existsByEleveIdAndDatePassageAndResultat(
                eq(1L), any(LocalDate.class), eq(ResultatScan.ACCORDE))).thenReturn(false);
        when(passageRepository.save(any())).thenReturn(passage(e, ResultatScan.ACCORDE, null));

        ScanResultDTO result = scanService.scanner(e.getQrCodeToken().toString());

        assertThat(result.resultat()).isEqualTo(ResultatScan.ACCORDE);
        assertThat(result.acces()).isEqualTo("ACCORDÉ");
        assertThat(result.motifRefus()).isNull();
    }

    @Test
    void scanner_accorde_quandStatutGrace() {
        Eleve e = eleve(2L, StatutAcces.GRACE);
        when(eleveRepository.findByQrCodeTokenAndActifTrue(e.getQrCodeToken()))
                .thenReturn(Optional.of(e));
        when(passageRepository.existsByEleveIdAndDatePassageAndResultat(
                eq(2L), any(LocalDate.class), eq(ResultatScan.ACCORDE))).thenReturn(false);
        when(passageRepository.save(any())).thenReturn(passage(e, ResultatScan.ACCORDE, null));

        ScanResultDTO result = scanService.scanner(e.getQrCodeToken().toString());

        assertThat(result.resultat()).isEqualTo(ResultatScan.ACCORDE);
    }

    @Test
    void scanner_refuse_quandStatutSuspendu() {
        Eleve e = eleve(3L, StatutAcces.SUSPENDU);
        when(eleveRepository.findByQrCodeTokenAndActifTrue(e.getQrCodeToken()))
                .thenReturn(Optional.of(e));
        when(passageRepository.save(any()))
                .thenReturn(passage(e, ResultatScan.REFUSE, MotifRefus.STATUT_SUSPENDU));

        ScanResultDTO result = scanService.scanner(e.getQrCodeToken().toString());

        assertThat(result.resultat()).isEqualTo(ResultatScan.REFUSE);
        assertThat(result.motifRefus()).isEqualTo(MotifRefus.STATUT_SUSPENDU);
        // Doublon non vérifié sur élève suspendu
        verify(passageRepository, never())
                .existsByEleveIdAndDatePassageAndResultat(any(), any(), any());
    }

    @Test
    void scanner_refuse_quandStatutEnAttentePaiement() {
        Eleve e = eleve(4L, StatutAcces.EN_ATTENTE_PAIEMENT);
        when(eleveRepository.findByQrCodeTokenAndActifTrue(e.getQrCodeToken()))
                .thenReturn(Optional.of(e));
        when(passageRepository.save(any()))
                .thenReturn(passage(e, ResultatScan.REFUSE, MotifRefus.STATUT_EN_ATTENTE_PAIEMENT));

        ScanResultDTO result = scanService.scanner(e.getQrCodeToken().toString());

        assertThat(result.motifRefus()).isEqualTo(MotifRefus.STATUT_EN_ATTENTE_PAIEMENT);
    }

    @Test
    void scanner_refuse_quandDoublonPassageMemeJour() {
        Eleve e = eleve(5L, StatutAcces.AUTORISE);
        when(eleveRepository.findByQrCodeTokenAndActifTrue(e.getQrCodeToken()))
                .thenReturn(Optional.of(e));
        when(passageRepository.existsByEleveIdAndDatePassageAndResultat(
                eq(5L), any(LocalDate.class), eq(ResultatScan.ACCORDE))).thenReturn(true);
        when(passageRepository.save(any()))
                .thenReturn(passage(e, ResultatScan.REFUSE, MotifRefus.DOUBLON_PASSAGE));

        ScanResultDTO result = scanService.scanner(e.getQrCodeToken().toString());

        assertThat(result.resultat()).isEqualTo(ResultatScan.REFUSE);
        assertThat(result.motifRefus()).isEqualTo(MotifRefus.DOUBLON_PASSAGE);
    }

    @Test
    void scanner_leveException_quandTokenInconnu() {
        UUID token = UUID.randomUUID();
        when(eleveRepository.findByQrCodeTokenAndActifTrue(token)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scanService.scanner(token.toString()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void scanner_leveException_quandFormatTokenInvalide() {
        assertThatThrownBy(() -> scanService.scanner("pas-un-uuid-valide"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Format QR code invalide");
    }

    @Test
    void listerPassages_restreintAuxEnfantsDuParent_quandRolePARENT() {
        Utilisateur parent = principal(Role.PARENT);
        when(parentRepository.findEnfantIdsByUtilisateurId(42L)).thenReturn(List.of(1L));

        Eleve e = eleve(1L, StatutAcces.AUTORISE);
        Pageable pageable = PageRequest.of(0, 50);
        Page<PassageRefectoire> page = new PageImpl<>(List.of(passage(e, ResultatScan.ACCORDE, null)), pageable, 1);
        when(passageRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<PassageResponseDTO> result = scanService.listerPassages(
                null, null, null, null, null, null, pageable, parent);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(parentRepository).findEnfantIdsByUtilisateurId(42L);
    }

    @Test
    void listerPassages_retournePageVide_quandParentSansEnfant() {
        Utilisateur parent = principal(Role.PARENT);
        when(parentRepository.findEnfantIdsByUtilisateurId(42L)).thenReturn(List.of());
        Pageable pageable = PageRequest.of(0, 50);

        Page<PassageResponseDTO> result = scanService.listerPassages(
                null, null, null, null, null, null, pageable, parent);

        assertThat(result.getTotalElements()).isZero();
        verify(passageRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void listerPassages_neRestreintPas_quandRoleNonParent() {
        Utilisateur admin = principal(Role.ADMIN);
        Pageable pageable = PageRequest.of(0, 50);
        when(passageRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        scanService.listerPassages(null, null, null, null, null, null, pageable, admin);

        verify(parentRepository, never()).findEnfantIdsByUtilisateurId(any(Long.class));
    }
}
