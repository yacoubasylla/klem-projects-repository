package com.klem.referentielapi.textereglementaire.api.controller;

import com.klem.referentielapi.shared.api.PageResponse;
import com.klem.referentielapi.textereglementaire.api.request.CreateTexteReglementaireRequest;
import com.klem.referentielapi.textereglementaire.api.request.UpdateTexteReglementaireStatutRequest;
import com.klem.referentielapi.textereglementaire.api.response.TexteReglementaireResponse;
import com.klem.referentielapi.textereglementaire.application.service.TexteReglementaireService;
import com.klem.referentielapi.textereglementaire.domain.model.TexteReglementaire;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

/**
 * Controller mince — la logique métier vit dans {@link TexteReglementaireService}
 * ({@code KLEM_MASTER_SYSTEM_DIRECTIVE.md} §5).
 * <p>
 * Lecture ouverte aux trois rôles (Lecteur/Editeur/Admin) — le référentiel est public en lecture
 * une fois authentifié (spec §3). Écriture (proposition + transition de statut) restreinte à
 * Editeur/Admin. Le contrôle de l'offre souscrite par un {@code Lecteur} (spec §3, `cas_metier.md`
 * §8) n'est pas implémenté à ce stade — dépendrait d'un appel à {@code core-api}, hors périmètre
 * MVP tant qu'aucun besoin réel ne l'exige (pas d'anticipation).
 */
@RestController
@RequestMapping("/api/v1/textes-reglementaires")
public class TexteReglementaireController {

    private final TexteReglementaireService service;

    public TexteReglementaireController(TexteReglementaireService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EDITEUR', 'ADMIN')")
    public ResponseEntity<TexteReglementaireResponse> propose(
            @Valid @RequestBody CreateTexteReglementaireRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        TexteReglementaire texte = service.propose(
                request.titre(), request.type(), request.datePublication(),
                request.reference(), request.domaine(), request.urlSource(), jwt.getSubject());
        TexteReglementaireResponse body = TexteReglementaireResponse.from(texte);
        return ResponseEntity.created(URI.create("/api/v1/textes-reglementaires/" + texte.getId())).body(body);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LECTEUR', 'EDITEUR', 'ADMIN')")
    public TexteReglementaireResponse get(@PathVariable UUID id) {
        return TexteReglementaireResponse.from(service.get(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LECTEUR', 'EDITEUR', 'ADMIN')")
    public PageResponse<TexteReglementaireResponse> list(Pageable pageable) {
        return PageResponse.from(service.list(pageable).map(TexteReglementaireResponse::from));
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('EDITEUR', 'ADMIN')")
    public TexteReglementaireResponse updateStatut(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTexteReglementaireStatutRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return TexteReglementaireResponse.from(service.changeStatus(id, request.statut(), jwt.getSubject()));
    }
}
