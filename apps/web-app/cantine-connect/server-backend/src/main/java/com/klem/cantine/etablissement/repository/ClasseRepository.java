package com.klem.cantine.etablissement.repository;

import com.klem.cantine.etablissement.entity.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ClasseRepository extends JpaRepository<Classe, Long> {

    @Query("SELECT c FROM Classe c JOIN FETCH c.niveau n WHERE n.etablissement.id = :etablissementId AND c.anneeScolaire = :anneeScolaire")
    List<Classe> findByEtablissementAndAnnee(Long etablissementId, String anneeScolaire);

    List<Classe> findByNiveauId(Long niveauId);

    boolean existsByNiveauId(Long niveauId);

    boolean existsByNiveau_EtablissementId(Long etablissementId);
}
