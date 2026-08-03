package com.klem.cantine.parent.repository;

import com.klem.cantine.parent.entity.Parent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    @Query("SELECT p FROM Parent p JOIN FETCH p.utilisateur WHERE p.utilisateur.id = :utilisateurId")
    Optional<Parent> findByUtilisateurId(@Param("utilisateurId") Long utilisateurId);

    @Query("SELECT p FROM Parent p JOIN FETCH p.utilisateur LEFT JOIN FETCH p.enfants")
    Page<Parent> findAllWithDetails(Pageable pageable);

    // :search est garanti non-null par le service (branchement en Java) — évite le bug
    // Hibernate 6 de type inference sur paramètre JPQL null dans LOWER()/LIKE (voir ADR-007).
    // Recherche sur email OU téléphone (le téléphone n'a pas besoin de LOWER()).
    @Query("""
            SELECT p FROM Parent p JOIN FETCH p.utilisateur LEFT JOIN FETCH p.enfants
            WHERE LOWER(p.utilisateur.email) LIKE LOWER(CONCAT('%', :search, '%'))
               OR p.utilisateur.telephone LIKE CONCAT('%', :search, '%')
            """)
    Page<Parent> findAllWithDetailsBySearch(@Param("search") String search, Pageable pageable);

    boolean existsByUtilisateurId(Long utilisateurId);

    @Query("SELECT e.id FROM Parent p JOIN p.enfants e WHERE p.utilisateur.id = :utilisateurId")
    List<Long> findEnfantIdsByUtilisateurId(@Param("utilisateurId") Long utilisateurId);
}
