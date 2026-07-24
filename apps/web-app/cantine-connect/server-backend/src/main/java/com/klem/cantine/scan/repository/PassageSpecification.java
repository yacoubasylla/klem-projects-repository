package com.klem.cantine.scan.repository;

import com.klem.cantine.scan.entity.PassageRefectoire;
import com.klem.cantine.scan.entity.ResultatScan;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PassageSpecification {

    private PassageSpecification() {}

    public static Specification<PassageRefectoire> withFilters(
            LocalDate dateDebut,
            LocalDate dateFin,
            Long etablissementId,
            ResultatScan resultat,
            String search) {
        return withFilters(dateDebut, dateFin, etablissementId, resultat, search, null);
    }

    public static Specification<PassageRefectoire> withFilters(
            LocalDate dateDebut,
            LocalDate dateFin,
            Long etablissementId,
            ResultatScan resultat,
            String search,
            List<Long> eleveIdsRestriction) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Plage de dates (obligatoire — toujours renseignée par le service)
            predicates.add(cb.between(root.get("datePassage"), dateDebut, dateFin));

            // Restriction aux élèves autorisés (ex : enfants du parent connecté)
            if (eleveIdsRestriction != null) {
                predicates.add(root.get("eleve").get("id").in(eleveIdsRestriction));
            }

            // Filtre établissement
            if (etablissementId != null) {
                predicates.add(cb.equal(
                        root.get("eleve").get("etablissement").get("id"),
                        etablissementId));
            }

            // Filtre résultat (ACCORDE / REFUSE)
            if (resultat != null) {
                predicates.add(cb.equal(root.get("resultat"), resultat));
            }

            // Recherche texte : nom, prénom ou matricule de l'élève
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("eleve").get("nom")), pattern),
                        cb.like(cb.lower(root.get("eleve").get("prenom")), pattern),
                        cb.like(cb.lower(root.get("eleve").get("matricule")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
