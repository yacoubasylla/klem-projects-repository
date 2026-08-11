package com.klem.referentielapi.operationcommerce.api.controller;

import com.klem.referentielapi.operationcommerce.api.request.AssociateDocumentRequest;
import com.klem.referentielapi.operationcommerce.api.request.CreateOperationCommerceRequest;
import com.klem.referentielapi.operationcommerce.api.request.UpdateOperationCommerceStatutRequest;
import com.klem.referentielapi.operationcommerce.api.response.OperationCommerceResponse;
import com.klem.referentielapi.operationcommerce.application.service.OperationCommerceService;
import com.klem.referentielapi.operationcommerce.domain.model.OperationCommerce;
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

/**
 * Controller mince — la logique métier vit dans {@link OperationCommerceService}.
 * <p>
 * {@code GET /{code}/documents} (par code métier, pas UUID) est le contrat de consultation exposé à
 * CLEAR-COMPLY (spec §4.3) — ne pas le renommer/déplacer sans coordonner avec cette équipe.
 */
@RestController
@RequestMapping("/api/v1/operations")
public class OperationCommerceController {

    private final OperationCommerceService service;

    public OperationCommerceController(OperationCommerceService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EDITEUR', 'ADMIN')")
    public ResponseEntity<OperationCommerceResponse> propose(
            @Valid @RequestBody CreateOperationCommerceRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        OperationCommerce operation = service.propose(
                request.nom(), request.code(), request.type(), request.procedureId(), jwt.getSubject());
        OperationCommerceResponse body = OperationCommerceResponse.from(operation);
        return ResponseEntity.created(URI.create("/api/v1/operations/" + operation.getId())).body(body);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LECTEUR', 'EDITEUR', 'ADMIN')")
    public OperationCommerceResponse get(@PathVariable UUID id) {
        return OperationCommerceResponse.from(service.get(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LECTEUR', 'EDITEUR', 'ADMIN')")
    public PageResponse<OperationCommerceResponse> list(Pageable pageable) {
        return PageResponse.from(service.list(pageable).map(OperationCommerceResponse::from));
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('EDITEUR', 'ADMIN')")
    public OperationCommerceResponse updateStatut(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOperationCommerceStatutRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return OperationCommerceResponse.from(service.changeStatus(id, request.statut(), jwt.getSubject()));
    }

    @PostMapping("/{id}/documents/{documentId}")
    @PreAuthorize("hasAnyRole('EDITEUR', 'ADMIN')")
    public ResponseEntity<Void> associateDocument(
            @PathVariable UUID id, @PathVariable UUID documentId,
            @Valid @RequestBody(required = false) AssociateDocumentRequest request) {
        String condition = request != null ? request.conditionApplicabilite() : null;
        service.associateDocument(id, documentId, condition);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/documents/{documentId}")
    @PreAuthorize("hasAnyRole('EDITEUR', 'ADMIN')")
    public ResponseEntity<Void> dissociateDocument(@PathVariable UUID id, @PathVariable UUID documentId) {
        service.dissociateDocument(id, documentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{code}/documents")
    @PreAuthorize("hasAnyRole('LECTEUR', 'EDITEUR', 'ADMIN')")
    public List<UUID> listDocumentsByCode(@PathVariable String code) {
        return service.listDocumentIdsByCode(code);
    }
}
