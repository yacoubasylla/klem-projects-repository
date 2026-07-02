package com.klem.cantine.eleve.controller;

import com.klem.cantine.common.ApiResponse;
import com.klem.cantine.eleve.dto.EleveRequestDTO;
import com.klem.cantine.eleve.entity.StatutAcces;
import com.klem.cantine.eleve.service.EleveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/eleves")
@RequiredArgsConstructor
public class EleveController {

    private final EleveService eleveService;

    @GetMapping
    @PreAuthorize("!hasRole('PARENT')")
    public ResponseEntity<ApiResponse<?>> lister(
            @RequestParam(required = false) Long etablissementId,
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) StatutAcces statut,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "nom") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(eleveService.lister(etablissementId, classeId, statut, search, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("!hasRole('PARENT')")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(eleveService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> creer(@Valid @RequestBody EleveRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(eleveService.creer(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> modifier(@PathVariable Long id, @Valid @RequestBody EleveRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Élève mis à jour", eleveService.modifier(id, dto)));
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GESTIONNAIRE')")
    public ResponseEntity<ApiResponse<?>> changerStatut(
            @PathVariable Long id,
            @RequestParam StatutAcces statut) {
        return ResponseEntity.ok(ApiResponse.ok("Statut mis à jour", eleveService.changerStatut(id, statut)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        eleveService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimerDefinitivement(@PathVariable Long id) {
        eleveService.supprimerDefinitivement(id);
        return ResponseEntity.noContent().build();
    }
}
