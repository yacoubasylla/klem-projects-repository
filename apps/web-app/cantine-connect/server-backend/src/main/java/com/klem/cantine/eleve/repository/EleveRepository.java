package com.klem.cantine.eleve.repository;

import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.eleve.entity.StatutAcces;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EleveRepository extends JpaRepository<Eleve, Long> {

    // Pagination filtrée — native query pour éviter le bug Hibernate 6 + PostgreSQL sur lower(bytea)
    @Query(value = """
            SELECT e.* FROM eleves e
            JOIN etablissements et ON et.id = e.etablissement_id
            JOIN classes c ON c.id = e.classe_id
            WHERE e.actif = true
              AND (CAST(:etablissementId AS bigint) IS NULL OR et.id = CAST(:etablissementId AS bigint))
              AND (CAST(:classeId AS bigint) IS NULL OR c.id = CAST(:classeId AS bigint))
              AND (CAST(:statut AS varchar) IS NULL OR e.statut_acces = CAST(:statut AS varchar))
              AND (CAST(:search AS varchar) IS NULL
                   OR LOWER(e.nom) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR LOWER(e.matricule) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%')))
            ORDER BY e.nom
            """,
           countQuery = """
            SELECT COUNT(*) FROM eleves e
            WHERE e.actif = true
              AND (CAST(:etablissementId AS bigint) IS NULL OR e.etablissement_id = CAST(:etablissementId AS bigint))
              AND (CAST(:classeId AS bigint) IS NULL OR e.classe_id = CAST(:classeId AS bigint))
              AND (CAST(:statut AS varchar) IS NULL OR e.statut_acces = CAST(:statut AS varchar))
              AND (CAST(:search AS varchar) IS NULL
                   OR LOWER(e.nom) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%'))
                   OR LOWER(e.matricule) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%')))
            """,
           nativeQuery = true)
    Page<Eleve> findAllWithFilters(
            @Param("etablissementId") Long etablissementId,
            @Param("classeId") Long classeId,
            @Param("statut") String statut,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT e FROM Eleve e JOIN FETCH e.etablissement JOIN FETCH e.classe WHERE e.id = :id AND e.actif = true")
    Optional<Eleve> findByIdActive(Long id);

    Optional<Eleve> findByQrCodeToken(UUID qrCodeToken);

    // Utilisé par le scan réfectoire — filtre actif=true pour éviter les élèves supprimés
    @Query("SELECT e FROM Eleve e WHERE e.qrCodeToken = :token AND e.actif = true")
    Optional<Eleve> findByQrCodeTokenAndActifTrue(@Param("token") UUID token);

    // Cache offline : tous les élèves actifs avec leurs associations (évite N+1)
    @Query("SELECT e FROM Eleve e JOIN FETCH e.etablissement JOIN FETCH e.classe " +
           "WHERE e.actif = true ORDER BY e.etablissement.id, e.classe.id, e.nom")
    List<Eleve> findAllActiveWithDetails();

    boolean existsByMatricule(String matricule);

    boolean existsByEtablissementIdAndActifTrue(Long etablissementId);

    boolean existsByClasseIdAndActifTrue(Long classeId);

    long countByActifTrue();

    long countByStatutAccesAndActifTrue(StatutAcces statut);
}
