package com.klem.cantine.etablissement.controller;

import com.klem.cantine.common.ApiResponse;
import com.klem.cantine.etablissement.dto.ClasseRequestDTO;
import com.klem.cantine.etablissement.dto.EtablissementRequestDTO;
import com.klem.cantine.etablissement.dto.NiveauRequestDTO;
import com.klem.cantine.etablissement.service.EtablissementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/etablissements")
@RequiredArgsConstructor
public class EtablissementController {

    private final EtablissementService etablissementService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> creer(@Valid @RequestBody EtablissementRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(etablissementService.creer(dto)));
    }

    @GetMapping
    @PreAuthorize("!hasRole('PARENT')")
    public ResponseEntity<ApiResponse<?>> lister() {
        return ResponseEntity.ok(ApiResponse.ok(etablissementService.listerActifs()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("!hasRole('PARENT')")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(etablissementService.getById(id)));
    }

    @GetMapping("/{id}/classes")
    @PreAuthorize("!hasRole('PARENT')")
    public ResponseEntity<ApiResponse<?>> getClasses(
            @PathVariable Long id,
            @RequestParam(defaultValue = "2025-2026") String anneeScolaire) {
        return ResponseEntity.ok(ApiResponse.ok(etablissementService.getClassesByEtablissement(id, anneeScolaire)));
    }

    @GetMapping("/{id}/niveaux")
    @PreAuthorize("!hasRole('PARENT')")
    public ResponseEntity<ApiResponse<?>> getNiveaux(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(etablissementService.getNiveauxAvecClasses(id)));
    }

    @PostMapping("/{id}/niveaux")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> creerNiveau(
            @PathVariable Long id,
            @Valid @RequestBody NiveauRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(etablissementService.creerNiveau(id, dto)));
    }

    @PostMapping("/niveaux/{niveauId}/classes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> creerClasse(
            @PathVariable Long niveauId,
            @Valid @RequestBody ClasseRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(etablissementService.creerClasse(niveauId, dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> modifier(
            @PathVariable Long id,
            @Valid @RequestBody EtablissementRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(etablissementService.modifier(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> supprimer(@PathVariable Long id) {
        etablissementService.supprimer(id);
        return ResponseEntity.ok(ApiResponse.ok("Établissement supprimé"));
    }

    @PutMapping("/niveaux/{niveauId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> modifierNiveau(
            @PathVariable Long niveauId,
            @Valid @RequestBody NiveauRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(etablissementService.modifierNiveau(niveauId, dto)));
    }

    @DeleteMapping("/niveaux/{niveauId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> supprimerNiveau(@PathVariable Long niveauId) {
        etablissementService.supprimerNiveau(niveauId);
        return ResponseEntity.ok(ApiResponse.ok("Niveau supprimé"));
    }

    @PutMapping("/classes/{classeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> modifierClasse(
            @PathVariable Long classeId,
            @Valid @RequestBody ClasseRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(etablissementService.modifierClasse(classeId, dto)));
    }

    @DeleteMapping("/classes/{classeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> supprimerClasse(@PathVariable Long classeId) {
        etablissementService.supprimerClasse(classeId);
        return ResponseEntity.ok(ApiResponse.ok("Classe supprimée"));
    }
}
