package com.klem.cantine.etablissement.repository;

import com.klem.cantine.etablissement.entity.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NiveauRepository extends JpaRepository<Niveau, Long> {

    List<Niveau> findByEtablissementIdOrderByOrdre(Long etablissementId);

    boolean existsByEtablissementId(Long etablissementId);
}
