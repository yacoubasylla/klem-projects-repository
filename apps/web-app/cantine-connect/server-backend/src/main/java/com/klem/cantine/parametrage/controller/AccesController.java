package com.klem.cantine.parametrage.controller;

import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.common.ApiResponse;
import com.klem.cantine.parametrage.dto.DemandeAccesRequestDTO;
import com.klem.cantine.parametrage.dto.RejeterDemandeRequestDTO;
import com.klem.cantine.parametrage.entity.StatutDemande;
import com.klem.cantine.parametrage.service.DemandeAccesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * `POST /` est public (self-service, aucun compte créé — voir DemandeAccesService).
 * Les autres endpoints (liste, validation, rejet) sont réservés à l'ADMIN.
 */
@RestController
@RequestMapping("/api/v1/demandes-acces")
@RequiredArgsConstructor
public class AccesController {

    private final DemandeAccesService demandeAccesService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> soumettre(@Valid @RequestBody DemandeAccesRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(demandeAccesService.soumettre(dto)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> lister(
            @RequestParam(required = false) StatutDemande statut,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "dateSoumission") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(demandeAccesService.lister(statut, search, pageable)));
    }

    @PatchMapping("/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> valider(
            @PathVariable Long id, @AuthenticationPrincipal Utilisateur admin) {
        return ResponseEntity.ok(ApiResponse.ok("Demande validée", demandeAccesService.valider(id, admin)));
    }

    @PatchMapping("/{id}/rejeter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> rejeter(
            @PathVariable Long id, @Valid @RequestBody RejeterDemandeRequestDTO dto,
            @AuthenticationPrincipal Utilisateur admin) {
        return ResponseEntity.ok(ApiResponse.ok("Demande rejetée", demandeAccesService.rejeter(id, dto.motif(), admin)));
    }
}
