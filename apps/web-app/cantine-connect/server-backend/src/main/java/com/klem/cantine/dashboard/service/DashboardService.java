package com.klem.cantine.dashboard.service;

import com.klem.cantine.dashboard.dto.DashboardStatsDTO;
import com.klem.cantine.dashboard.dto.JourPassageDTO;
import com.klem.cantine.scan.dto.PassageResponseDTO;
import com.klem.cantine.eleve.entity.StatutAcces;
import com.klem.cantine.eleve.repository.EleveRepository;
import com.klem.cantine.etablissement.repository.EtablissementRepository;
import com.klem.cantine.paiement.entity.StatutPaiement;
import com.klem.cantine.paiement.repository.TransactionPaiementRepository;
import com.klem.cantine.scan.entity.ResultatScan;
import com.klem.cantine.scan.repository.PassageRefectoireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final EtablissementRepository etablissementRepository;
    private final EleveRepository eleveRepository;
    private final PassageRefectoireRepository passageRepository;
    private final TransactionPaiementRepository transactionRepository;

    public DashboardStatsDTO getStats() {
        LocalDate today = LocalDate.now();
        LocalDate debut7Jours = today.minusDays(6);
        LocalDateTime debutMois = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMois = today.plusDays(1).atStartOfDay();

        long nbEtablissements = etablissementRepository.countByActifTrue();

        long totalEleves = eleveRepository.countByActifTrue();
        long autorises   = eleveRepository.countByStatutAccesAndActifTrue(StatutAcces.AUTORISE);
        long grace       = eleveRepository.countByStatutAccesAndActifTrue(StatutAcces.GRACE);
        long enAttente   = eleveRepository.countByStatutAccesAndActifTrue(StatutAcces.EN_ATTENTE_PAIEMENT);
        long suspendus   = eleveRepository.countByStatutAccesAndActifTrue(StatutAcces.SUSPENDU);

        long passagesAccordes = passageRepository.countByDatePassageAndResultat(today, ResultatScan.ACCORDE);
        long passagesRefuses  = passageRepository.countByDatePassageAndResultat(today, ResultatScan.REFUSE);

        List<JourPassageDTO> tendance = buildTendance(debut7Jours, today);

        List<Object[]> payStatsRows = transactionRepository.statsAcceptesPeriode(debutMois, finMois);
        Object[] payStats = payStatsRows.isEmpty() ? null : payStatsRows.get(0);
        long nbPaiementsMois = (payStats != null && payStats[0] != null)
                ? ((Number) payStats[0]).longValue() : 0L;
        BigDecimal montantMois = (payStats != null && payStats[1] != null)
                ? new BigDecimal(payStats[1].toString()) : BigDecimal.ZERO;
        long nbPaiementsEnAttente = transactionRepository.countByStatut(StatutPaiement.EN_ATTENTE);

        List<PassageResponseDTO> derniersPassages = passageRepository
                .findTop5ByDatePassageOrderByHeurePassageDesc(today)
                .stream()
                .map(PassageResponseDTO::from)
                .toList();

        return new DashboardStatsDTO(
            nbEtablissements,
            totalEleves, autorises, grace, enAttente, suspendus,
            passagesAccordes + passagesRefuses,
            passagesAccordes, passagesRefuses,
            tendance,
            nbPaiementsMois, montantMois, nbPaiementsEnAttente,
            derniersPassages
        );
    }

    private List<JourPassageDTO> buildTendance(LocalDate debut, LocalDate fin) {
        List<Object[]> raw = passageRepository.countByDateRangeGrouped(debut, fin);

        Map<LocalDate, long[]> byDay = new LinkedHashMap<>();
        for (LocalDate d = debut; !d.isAfter(fin); d = d.plusDays(1)) {
            byDay.put(d, new long[]{0L, 0L});
        }
        for (Object[] row : raw) {
            LocalDate date = (LocalDate) row[0];
            ResultatScan resultat = (ResultatScan) row[1];
            long count = ((Number) row[2]).longValue();
            long[] counts = byDay.computeIfAbsent(date, k -> new long[]{0L, 0L});
            if (resultat == ResultatScan.ACCORDE) counts[0] = count;
            else counts[1] = count;
        }

        List<JourPassageDTO> result = new ArrayList<>();
        byDay.forEach((date, counts) -> result.add(new JourPassageDTO(date, counts[0], counts[1])));
        return result;
    }
}
