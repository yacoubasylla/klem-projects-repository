package com.klem.referentielapi.textereglementaire.application.service;

import com.klem.referentielapi.shared.domain.StatutPublication;
import com.klem.referentielapi.textereglementaire.application.port.TexteReglementaireRepository;
import com.klem.referentielapi.textereglementaire.domain.exception.TexteReglementaireNotFoundException;
import com.klem.referentielapi.textereglementaire.domain.model.TexteReglementaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Cas d'usage du domaine {@code textereglementaire} — transactions définies ici, jamais dans le
 * controller ({@code KLEM_MASTER_SYSTEM_DIRECTIVE.md} §5).
 */
@Service
@Transactional(readOnly = true)
public class TexteReglementaireService {

    private final TexteReglementaireRepository repository;

    public TexteReglementaireService(TexteReglementaireRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TexteReglementaire propose(String titre, String type, LocalDate datePublication,
                                       String reference, String domaine, String urlSource,
                                       String createdBy) {
        TexteReglementaire texte = TexteReglementaire.propose(
                titre, type, datePublication, reference, domaine, urlSource, createdBy);
        return repository.save(texte);
    }

    public TexteReglementaire get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new TexteReglementaireNotFoundException(id));
    }

    public Page<TexteReglementaire> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional
    public TexteReglementaire changeStatus(UUID id, StatutPublication newStatus, String actorSubject) {
        TexteReglementaire texte = get(id);
        texte.changeStatus(newStatus, actorSubject);
        return repository.save(texte);
    }

    /**
     * Vérification d'existence exposée au domaine {@code procedure} (jointure
     * {@code procedure_texte}) — volontairement plus étroite que {@link #get}, qui expose l'entité
     * {@link TexteReglementaire} et lève {@link TexteReglementaireNotFoundException}, tous deux
     * internes à ce domaine (motif « lecture peer-à-peer », voir {@code PackageBoundaryRulesTest}).
     */
    public boolean exists(UUID id) {
        return repository.existsById(id);
    }
}
