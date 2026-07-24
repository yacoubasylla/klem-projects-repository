package com.klem.cantine.dashboard.dto;

import com.klem.cantine.scan.dto.PassageResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public record DashboardStatsDTO(
    long nbEtablissements,
    long totalEleves,
    long autorises,
    long grace,
    long enAttente,
    long suspendus,
    long passagesAujourdhui,
    long passagesAccordes,
    long passagesRefuses,
    List<JourPassageDTO> tendance7Jours,
    long nbPaiementsMois,
    BigDecimal montantPaiementsMois,
    long nbPaiementsEnAttente,
    List<PassageResponseDTO> derniersPassages
) {}
