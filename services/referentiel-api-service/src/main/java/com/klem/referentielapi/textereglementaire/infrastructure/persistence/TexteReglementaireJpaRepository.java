package com.klem.referentielapi.textereglementaire.infrastructure.persistence;

import com.klem.referentielapi.textereglementaire.application.port.TexteReglementaireRepository;
import com.klem.referentielapi.textereglementaire.domain.model.TexteReglementaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data implémente directement les méthodes de {@link TexteReglementaireRepository} à partir
 * de {@link JpaRepository} — aucune classe d'adaptation supplémentaire nécessaire tant que le port
 * reste ce sous-ensemble.
 */
interface TexteReglementaireJpaRepository extends JpaRepository<TexteReglementaire, UUID>, TexteReglementaireRepository {
}
