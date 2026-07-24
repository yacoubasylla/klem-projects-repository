package com.klem.cantine.scan.service;

import com.klem.cantine.auth.entity.Role;
import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.eleve.entity.StatutAcces;
import com.klem.cantine.eleve.repository.EleveRepository;
import com.klem.cantine.notification.NotificationService;
import com.klem.cantine.parametrage.service.ConfigurationService;
import com.klem.cantine.actionlog.annotation.Traceable;
import com.klem.cantine.actionlog.entity.TypeAction;
import com.klem.cantine.parent.repository.ParentRepository;
import com.klem.cantine.scan.dto.CacheEntreeDTO;
import com.klem.cantine.scan.dto.ModifierPassageRequestDTO;
import com.klem.cantine.scan.dto.PassageResponseDTO;
import com.klem.cantine.scan.dto.ScanResultDTO;
import com.klem.cantine.scan.entity.MotifRefus;
import com.klem.cantine.scan.entity.PassageRefectoire;
import com.klem.cantine.scan.entity.ResultatScan;
import com.klem.cantine.scan.repository.PassageSpecification;
import com.klem.cantine.scan.repository.PassageRefectoireRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ScanService {

    private final PassageRefectoireRepository passageRepository;
    private final EleveRepository eleveRepository;
    private final ParentRepository parentRepository;
    private final NotificationService notificationService;
    private final ConfigurationService configurationService;

    // ── Scan QR Code ─────────────────────────────────────────────────────────

    @Transactional
    public ScanResultDTO scanner(String qrCodeTokenStr) {
        UUID qrCodeToken = parseUUID(qrCodeTokenStr);

        Eleve eleve = eleveRepository.findByQrCodeTokenAndActifTrue(qrCodeToken)
                .orElseThrow(() -> new EntityNotFoundException("QR code non reconnu : " + qrCodeTokenStr));

        StatutAcces statut = eleve.getStatutAcces();

        // Accès refusé par statut
        if (statut == StatutAcces.SUSPENDU) {
            return enregistrer(eleve, qrCodeToken, ResultatScan.REFUSE, MotifRefus.STATUT_SUSPENDU);
        }
        if (statut == StatutAcces.EN_ATTENTE_PAIEMENT) {
            return enregistrer(eleve, qrCodeToken, ResultatScan.REFUSE, MotifRefus.STATUT_EN_ATTENTE_PAIEMENT);
        }

        // AUTORISE ou GRACE — vérifier le doublon du jour
        LocalDate today = LocalDate.now();
        boolean dejaAccorde = passageRepository
                .existsByEleveIdAndDatePassageAndResultat(eleve.getId(), today, ResultatScan.ACCORDE);

        if (dejaAccorde) {
            return enregistrer(eleve, qrCodeToken, ResultatScan.REFUSE, MotifRefus.DOUBLON_PASSAGE);
        }

        // Mode CREDITS : vérifier et débiter le solde
        if ("CREDITS".equalsIgnoreCase(configurationService.getValeur("MODE_PAIEMENT"))) {
            BigDecimal tarif = parseTarif(configurationService.getValeur("TARIF_REPAS"));
            if (eleve.getSolde().compareTo(tarif) < 0) {
                return enregistrer(eleve, qrCodeToken, ResultatScan.REFUSE, MotifRefus.SOLDE_INSUFFISANT);
            }
            eleve.setSolde(eleve.getSolde().subtract(tarif));
            eleveRepository.save(eleve);
            log.info("Solde élève {} débité de {} FCFA → solde restant {}",
                    eleve.getId(), tarif, eleve.getSolde());
        }

        ScanResultDTO resultat = enregistrer(eleve, qrCodeToken, ResultatScan.ACCORDE, null);
        notificationService.notifierPassageCantine(eleve);
        return resultat;
    }

    // ── Cache offline (téléchargeable par les appareils de scan) ─────────────

    public List<CacheEntreeDTO> getCacheOffline() {
        return eleveRepository.findAllActiveWithDetails()
                .stream()
                .map(CacheEntreeDTO::from)
                .toList();
    }

    // ── Historique des passages ───────────────────────────────────────────────

    public Page<PassageResponseDTO> listerPassages(
            LocalDate date, LocalDate dateDebut, LocalDate dateFin,
            Long etablissementId, ResultatScan resultat, String search,
            Pageable pageable, Utilisateur principal) {

        LocalDate debut, fin;
        if (dateDebut != null && dateFin != null) {
            debut = dateDebut;
            fin   = dateFin;
        } else {
            LocalDate d = date != null ? date : LocalDate.now();
            debut = d;
            fin   = d;
        }

        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;

        List<Long> eleveIdsRestriction = null;
        if (principal.getRole() == Role.PARENT) {
            eleveIdsRestriction = parentRepository.findEnfantIdsByUtilisateurId(principal.getId());
            if (eleveIdsRestriction.isEmpty()) {
                return Page.empty(pageable);
            }
        }

        var spec = PassageSpecification.withFilters(debut, fin, etablissementId, resultat, searchParam, eleveIdsRestriction);
        return passageRepository.findAll(spec, pageable).map(PassageResponseDTO::from);
    }

    // ── Logique d'enregistrement ──────────────────────────────────────────────

    private ScanResultDTO enregistrer(Eleve eleve, UUID qrCodeToken,
                                       ResultatScan resultat, MotifRefus motif) {
        LocalDateTime now = LocalDateTime.now();
        PassageRefectoire passage = PassageRefectoire.builder()
                .eleve(eleve)
                .qrCodeToken(qrCodeToken)
                .datePassage(now.toLocalDate())
                .heurePassage(now)
                .resultat(resultat)
                .motifRefus(motif)
                .build();
        passage = passageRepository.save(passage);
        log.info("Scan {} — élève {} ({}) — motif={}",
                resultat, eleve.getId(), eleve.getMatricule(), motif);
        return ScanResultDTO.from(passage);
    }

    private UUID parseUUID(String token) {
        try {
            return UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Format QR code invalide");
        }
    }

    // ── Modification / Suppression administrateur ─────────────────────────────

    @Traceable(action = TypeAction.UPDATE, entite = "PassageRefectoire")
    @Transactional
    public PassageResponseDTO modifierPassage(Long id, ModifierPassageRequestDTO dto) {
        PassageRefectoire p = passageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Passage introuvable : " + id));
        if (dto.resultat() != null) p.setResultat(dto.resultat());
        p.setMotifRefus(dto.motifRefus()); // null autorisé pour effacer le motif
        return PassageResponseDTO.from(passageRepository.save(p));
    }

    @Traceable(action = TypeAction.DELETE, entite = "PassageRefectoire")
    @Transactional
    public void supprimerPassage(Long id) {
        if (!passageRepository.existsById(id)) {
            throw new EntityNotFoundException("Passage introuvable : " + id);
        }
        passageRepository.deleteById(id);
    }

    private BigDecimal parseTarif(String valeur) {
        try {
            return new BigDecimal(valeur);
        } catch (NumberFormatException e) {
            return new BigDecimal("500");
        }
    }
}
