package com.klem.cantine.paiement.repository;

import com.klem.cantine.paiement.entity.StatutPaiement;
import com.klem.cantine.paiement.entity.TransactionPaiement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionPaiementRepository extends JpaRepository<TransactionPaiement, Long> {

    Optional<TransactionPaiement> findByReferenceInterne(String referenceInterne);

    Page<TransactionPaiement> findByEleveId(Long eleveId, Pageable pageable);

    boolean existsByEleveId(Long eleveId);

    Page<TransactionPaiement> findByStatut(StatutPaiement statut, Pageable pageable);

    // Requêtes natives — voir ADR-007 : Hibernate 6 + PostgreSQL échouent à inférer le type
    // d'un paramètre JPQL nullable utilisé dans LOWER()/LIKE ("operator does not exist: lower(bytea)").
    // Le CAST explicite (CAST(:param AS ...)) contourne le problème.
    @Query(value = """
            SELECT t.* FROM transactions_paiement t
            JOIN eleves e ON e.id = t.eleve_id
            WHERE (CAST(:eleveId AS bigint) IS NULL OR e.id = CAST(:eleveId AS bigint))
              AND (CAST(:statut AS varchar) IS NULL OR t.statut = CAST(:statut AS varchar))
              AND (CAST(:operateur AS varchar) IS NULL OR t.operateur = CAST(:operateur AS varchar))
              AND (CAST(:dateDebut AS date) IS NULL OR t.date_creation >= CAST(:dateDebut AS date))
              AND (CAST(:dateFin AS date) IS NULL OR t.date_creation < CAST(:dateFin AS date) + INTERVAL '1 day')
              AND (CAST(:search AS varchar) IS NULL
                   OR LOWER(e.nom) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR LOWER(e.matricule) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%')))
            ORDER BY t.date_creation DESC
            """,
           countQuery = """
            SELECT COUNT(*) FROM transactions_paiement t
            JOIN eleves e ON e.id = t.eleve_id
            WHERE (CAST(:eleveId AS bigint) IS NULL OR e.id = CAST(:eleveId AS bigint))
              AND (CAST(:statut AS varchar) IS NULL OR t.statut = CAST(:statut AS varchar))
              AND (CAST(:operateur AS varchar) IS NULL OR t.operateur = CAST(:operateur AS varchar))
              AND (CAST(:dateDebut AS date) IS NULL OR t.date_creation >= CAST(:dateDebut AS date))
              AND (CAST(:dateFin AS date) IS NULL OR t.date_creation < CAST(:dateFin AS date) + INTERVAL '1 day')
              AND (CAST(:search AS varchar) IS NULL
                   OR LOWER(e.nom) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR LOWER(e.matricule) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%')))
            """,
           nativeQuery = true)
    Page<TransactionPaiement> findAllWithFilters(
            @Param("eleveId") Long eleveId, @Param("statut") String statut,
            @Param("operateur") String operateur, @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin, @Param("search") String search, Pageable pageable);

    @Query(value = """
            SELECT t.* FROM transactions_paiement t
            JOIN eleves e ON e.id = t.eleve_id
            WHERE e.id IN (:eleveIds)
              AND (CAST(:eleveId AS bigint) IS NULL OR e.id = CAST(:eleveId AS bigint))
              AND (CAST(:statut AS varchar) IS NULL OR t.statut = CAST(:statut AS varchar))
              AND (CAST(:operateur AS varchar) IS NULL OR t.operateur = CAST(:operateur AS varchar))
              AND (CAST(:dateDebut AS date) IS NULL OR t.date_creation >= CAST(:dateDebut AS date))
              AND (CAST(:dateFin AS date) IS NULL OR t.date_creation < CAST(:dateFin AS date) + INTERVAL '1 day')
              AND (CAST(:search AS varchar) IS NULL
                   OR LOWER(e.nom) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR LOWER(e.matricule) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%')))
            ORDER BY t.date_creation DESC
            """,
           countQuery = """
            SELECT COUNT(*) FROM transactions_paiement t
            JOIN eleves e ON e.id = t.eleve_id
            WHERE e.id IN (:eleveIds)
              AND (CAST(:eleveId AS bigint) IS NULL OR e.id = CAST(:eleveId AS bigint))
              AND (CAST(:statut AS varchar) IS NULL OR t.statut = CAST(:statut AS varchar))
              AND (CAST(:operateur AS varchar) IS NULL OR t.operateur = CAST(:operateur AS varchar))
              AND (CAST(:dateDebut AS date) IS NULL OR t.date_creation >= CAST(:dateDebut AS date))
              AND (CAST(:dateFin AS date) IS NULL OR t.date_creation < CAST(:dateFin AS date) + INTERVAL '1 day')
              AND (CAST(:search AS varchar) IS NULL
                   OR LOWER(e.nom) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR LOWER(e.matricule) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%')))
            """,
           nativeQuery = true)
    Page<TransactionPaiement> findAllWithFiltersForEleves(
            @Param("eleveIds") List<Long> eleveIds, @Param("eleveId") Long eleveId,
            @Param("statut") String statut, @Param("operateur") String operateur,
            @Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin,
            @Param("search") String search, Pageable pageable);

    long countByStatut(StatutPaiement statut);

    // Retourne toujours exactement une ligne (agrégat sans GROUP BY) — déclarer Object[]
    // directement ici plante en ClassCastException (Spring Data ne l'aplatit pas) : il faut
    // passer par List<Object[]> et prendre le premier élément côté service.
    @Query("SELECT COUNT(t), COALESCE(SUM(t.montant), 0) FROM TransactionPaiement t " +
           "WHERE t.statut = com.klem.cantine.paiement.entity.StatutPaiement.ACCEPTE " +
           "AND t.dateCreation >= :debut AND t.dateCreation < :fin")
    List<Object[]> statsAcceptesPeriode(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
