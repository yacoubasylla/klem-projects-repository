package com.klem.referentielapi.documentrequis.api.controller;

import com.klem.referentielapi.documentrequis.api.request.CreateDocumentRequisRequest;
import com.klem.referentielapi.documentrequis.api.request.UpdateDocumentRequisStatutRequest;
import com.klem.referentielapi.documentrequis.api.response.DocumentRequisResponse;
import com.klem.referentielapi.documentrequis.application.service.DocumentRequisService;
import com.klem.referentielapi.documentrequis.domain.model.DocumentRequis;
import com.klem.referentielapi.shared.api.PageResponse;
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

/** Controller mince — la logique métier vit dans {@link DocumentRequisService}. */
@RestController
@RequestMapping("/api/v1/documents-requis")
public class DocumentRequisController {

    private final DocumentRequisService service;

    public DocumentRequisController(DocumentRequisService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EDITEUR', 'ADMIN')")
    public ResponseEntity<DocumentRequisResponse> propose(
            @Valid @RequestBody CreateDocumentRequisRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        DocumentRequis document = service.propose(
                request.nom(), request.code(), request.description(), request.regleValidation(), jwt.getSubject());
        DocumentRequisResponse body = DocumentRequisResponse.from(document);
        return ResponseEntity.created(URI.create("/api/v1/documents-requis/" + document.getId())).body(body);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LECTEUR', 'EDITEUR', 'ADMIN')")
    public DocumentRequisResponse get(@PathVariable UUID id) {
        return DocumentRequisResponse.from(service.get(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LECTEUR', 'EDITEUR', 'ADMIN')")
    public PageResponse<DocumentRequisResponse> list(Pageable pageable) {
        return PageResponse.from(service.list(pageable).map(DocumentRequisResponse::from));
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('EDITEUR', 'ADMIN')")
    public DocumentRequisResponse updateStatut(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDocumentRequisStatutRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return DocumentRequisResponse.from(service.changeStatus(id, request.statut(), jwt.getSubject()));
    }
}
