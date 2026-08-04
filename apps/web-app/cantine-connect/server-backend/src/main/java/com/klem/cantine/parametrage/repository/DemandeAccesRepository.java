package com.klem.cantine.parametrage.repository;

import com.klem.cantine.parametrage.entity.DemandeAcces;
import com.klem.cantine.parametrage.entity.StatutDemande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DemandeAccesRepository extends JpaRepository<DemandeAcces, Long> {

    Page<DemandeAcces> findByStatut(StatutDemande statut, Pageable pageable);

    // :search et :statut sont garantis non-null/non-blank côté service avant l'appel (branchement
    // en Java) — évite le bug Hibernate 6 de type inference sur paramètre JPQL null dans
    // LOWER()/LIKE (voir ADR-007). Recherche sur nom, prénom, tous les numéros de téléphone,
    // email et résidence (ville/commune/quartier).
    @Query("""
            SELECT d FROM DemandeAcces d
            WHERE (:statut IS NULL OR d.statut = :statut)
              AND (
                   LOWER(d.nom) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(d.prenom) LIKE LOWER(CONCAT('%', :search, '%'))
                OR d.telephonePrincipal LIKE CONCAT('%', :search, '%')
                OR d.telephoneWhatsapp LIKE CONCAT('%', :search, '%')
                OR d.telephoneSecondaire LIKE CONCAT('%', :search, '%')
                OR LOWER(d.email) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(d.ville) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(d.commune) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(d.quartier) LIKE LOWER(CONCAT('%', :search, '%'))
              )
            """)
    Page<DemandeAcces> findAllBySearch(
            @Param("statut") StatutDemande statut, @Param("search") String search, Pageable pageable);
}
