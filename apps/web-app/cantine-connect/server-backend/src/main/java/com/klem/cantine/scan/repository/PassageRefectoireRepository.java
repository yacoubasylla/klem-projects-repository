package com.klem.cantine.scan.repository;

import com.klem.cantine.scan.entity.PassageRefectoire;
import com.klem.cantine.scan.entity.ResultatScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import com.klem.cantine.scan.entity.PassageRefectoire;

public interface PassageRefectoireRepository
        extends JpaRepository<PassageRefectoire, Long>,
                JpaSpecificationExecutor<PassageRefectoire> {

    // Vérification doublon : l'élève a-t-il déjà eu un passage ACCORDÉ aujourd'hui ?
    boolean existsByEleveIdAndDatePassageAndResultat(
            Long eleveId, LocalDate datePassage, ResultatScan resultat);

    boolean existsByEleveId(Long eleveId);

    // Comptage rapide pour le dashboard
    long countByDatePassageAndResultat(LocalDate date, ResultatScan resultat);

    // Derniers passages du jour pour le dashboard (5 max, plus récents en premier)
    List<PassageRefectoire> findTop5ByDatePassageOrderByHeurePassageDesc(LocalDate date);

    // Tendance 7 jours : résultats groupés par date et résultat
    @Query("SELECT p.datePassage, p.resultat, COUNT(p) FROM PassageRefectoire p " +
           "WHERE p.datePassage BETWEEN :debut AND :fin " +
           "GROUP BY p.datePassage, p.resultat ORDER BY p.datePassage ASC")
    List<Object[]> countByDateRangeGrouped(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    // Variantes restreintes à un ensemble d'élèves (tableau de bord PARENT — ses propres enfants)
    long countByDatePassageAndResultatAndEleveIdIn(LocalDate date, ResultatScan resultat, List<Long> eleveIds);

    List<PassageRefectoire> findTop5ByEleveIdInAndDatePassageOrderByHeurePassageDesc(
            List<Long> eleveIds, LocalDate date);

    @Query("SELECT p.datePassage, p.resultat, COUNT(p) FROM PassageRefectoire p " +
           "WHERE p.datePassage BETWEEN :debut AND :fin AND p.eleve.id IN :eleveIds " +
           "GROUP BY p.datePassage, p.resultat ORDER BY p.datePassage ASC")
    List<Object[]> countByDateRangeGroupedForEleves(
            @Param("debut") LocalDate debut, @Param("fin") LocalDate fin, @Param("eleveIds") List<Long> eleveIds);
}
