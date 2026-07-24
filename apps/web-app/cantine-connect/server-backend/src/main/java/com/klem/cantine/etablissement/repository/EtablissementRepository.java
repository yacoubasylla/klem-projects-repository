package com.klem.cantine.etablissement.repository;

import com.klem.cantine.etablissement.entity.Etablissement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface EtablissementRepository extends JpaRepository<Etablissement, Long> {

    List<Etablissement> findByActifTrue();

    long countByActifTrue();

    @Query("SELECT e FROM Etablissement e LEFT JOIN FETCH e.niveaux WHERE e.id = :id")
    java.util.Optional<Etablissement> findByIdWithNiveaux(Long id);
}
