package com.klem.referentielapi.procedure.api.controller;

import com.klem.referentielapi.procedure.api.request.CreateProcedureMetierRequest;
import com.klem.referentielapi.procedure.api.request.UpdateProcedureMetierStatutRequest;
import com.klem.referentielapi.procedure.api.response.ProcedureMetierResponse;
import com.klem.referentielapi.procedure.application.service.ProcedureMetierService;
import com.klem.referentielapi.procedure.domain.model.ProcedureMetier;
import com.klem.referentielapi.shared.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/** Controller mince — la logique métier vit dans {@link ProcedureMetierService}. */
@RestController
@RequestMapping("/api/v1/procedures")
public class ProcedureMetierController {

    private final ProcedureMetierService service;

    public ProcedureMetierController(ProcedureMetierService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EDITEUR', 'ADMIN')")
    public ResponseEntity<ProcedureMetierResponse> propose(
            @Valid @RequestBody CreateProcedureMetierRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        ProcedureMetier procedure = service.propose(
                request.nom(), request.code(), request.description(), request.acteurs(), jwt.getSubject());
        ProcedureMetierResponse body = ProcedureMetierResponse.from(procedure);
        return ResponseEntity.created(URI.create("/api/v1/procedures/" + procedure.getId())).body(body);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LECTEUR', 'EDITEUR', 'ADMIN')")
    public ProcedureMetierResponse get(@PathVariable UUID id) {
        return ProcedureMetierResponse.from(service.get(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LECTEUR', 'EDITEUR', 'ADMIN')")
    public PageResponse<ProcedureMetierResponse> list(Pageable pageable) {
        return PageResponse.from(service.list(pageable).map(ProcedureMetierResponse::from));
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('EDITEUR', 'ADMIN')")
    public ProcedureMetierResponse updateStatut(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProcedureMetierStatutRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ProcedureMetierResponse.from(service.changeStatus(id, request.statut(), jwt.getSubject()));
    }

    @PostMapping("/{id}/textes/{texteId}")
    @PreAuthorize("hasAnyRole('EDITEUR', 'ADMIN')")
    public ResponseEntity<Void> associateTexte(@PathVariable UUID id, @PathVariable UUID texteId) {
        service.associateTexte(id, texteId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/textes/{texteId}")
    @PreAuthorize("hasAnyRole('EDITEUR', 'ADMIN')")
    public ResponseEntity<Void> dissociateTexte(@PathVariable UUID id, @PathVariable UUID texteId) {
        service.dissociateTexte(id, texteId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/textes")
    @PreAuthorize("hasAnyRole('LECTEUR', 'EDITEUR', 'ADMIN')")
    public List<UUID> listTextes(@PathVariable UUID id) {
        return service.listTexteIds(id);
    }
}
