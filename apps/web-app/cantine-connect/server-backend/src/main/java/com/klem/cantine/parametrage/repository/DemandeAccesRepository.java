package com.klem.cantine.parametrage.repository;

import com.klem.cantine.parametrage.entity.DemandeAcces;
import com.klem.cantine.parametrage.entity.StatutDemande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandeAccesRepository extends JpaRepository<DemandeAcces, Long> {

    Page<DemandeAcces> findByStatut(StatutDemande statut, Pageable pageable);
}
