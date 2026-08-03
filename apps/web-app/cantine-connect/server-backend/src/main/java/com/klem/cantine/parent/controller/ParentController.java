package com.klem.cantine.parent.controller;

import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.common.ApiResponse;
import com.klem.cantine.eleve.dto.AjoutEnfantRequestDTO;
import com.klem.cantine.eleve.service.EleveService;
import com.klem.cantine.parent.dto.ParentRequestDTO;
import com.klem.cantine.parent.dto.ParentResponseDTO;
import com.klem.cantine.parent.service.ParentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parents")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;
    private final EleveService eleveService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ParentResponseDTO>>> lister(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(parentService.lister(search, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ParentResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(parentService.getById(id)));
    }

    @GetMapping("/moi")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<ParentResponseDTO>> getMoi(
            @AuthenticationPrincipal UserDetails userDetails) {
        // UserDetails.username == email → récupéré via l'entité Utilisateur
        // Le service résout l'utilisateur via le ParentRepository
        return ResponseEntity.ok(ApiResponse.ok(parentService.getMoi(
                ((com.klem.cantine.auth.entity.Utilisateur) userDetails).getId()
        )));
    }

    @PostMapping("/moi/enfants")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<?>> ajouterEnfant(
            @Valid @RequestBody AjoutEnfantRequestDTO dto,
            @AuthenticationPrincipal Utilisateur principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(eleveService.creerViaParent(principal, dto)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ParentResponseDTO>> creer(
            @Valid @RequestBody ParentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(parentService.creer(dto)));
    }

    @PutMapping("/{id}/enfants")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ParentResponseDTO>> modifierEnfants(
            @PathVariable Long id,
            @RequestBody List<Long> eleveIds) {
        return ResponseEntity.ok(ApiResponse.ok(parentService.modifierEnfants(id, eleveIds)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        parentService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
