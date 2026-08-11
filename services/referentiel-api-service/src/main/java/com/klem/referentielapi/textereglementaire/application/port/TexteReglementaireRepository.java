package com.klem.referentielapi.textereglementaire.application.port;

import com.klem.referentielapi.textereglementaire.domain.model.TexteReglementaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Port de persistance du domaine {@code textereglementaire} — l'implémentation vit en
 * {@code infrastructure.persistence}, jamais référencée directement en dehors de ce domaine.
 */
public interface TexteReglementaireRepository {

    TexteReglementaire save(TexteReglementaire texte);

    Optional<TexteReglementaire> findById(UUID id);

    Page<TexteReglementaire> findAll(Pageable pageable);

    boolean existsById(UUID id);
}
