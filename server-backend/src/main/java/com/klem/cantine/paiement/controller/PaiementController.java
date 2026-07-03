package com.klem.cantine.paiement.controller;

import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.common.ApiResponse;
import com.klem.cantine.paiement.dto.InitierPaiementRequestDTO;
import com.klem.cantine.paiement.dto.ModifierPaiementRequestDTO;
import com.klem.cantine.paiement.dto.PaiementResponseDTO;
import com.klem.cantine.paiement.entity.OperateurMobileMoney;
import com.klem.cantine.paiement.entity.StatutPaiement;
import com.klem.cantine.paiement.service.PaiementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;

    @PostMapping("/initier")
    public ResponseEntity<ApiResponse<PaiementResponseDTO>> initier(
            @Valid @RequestBody InitierPaiementRequestDTO dto,
            @AuthenticationPrincipal Utilisateur principal) {
        PaiementResponseDTO response = paiementService.initierPaiement(dto, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaiementResponseDTO>>> lister(
            @RequestParam(required = false) Long eleveId,
            @RequestParam(required = false) StatutPaiement statut,
            @RequestParam(required = false) OperateurMobileMoney operateur,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "dateCreation") Pageable pageable,
            @AuthenticationPrincipal Utilisateur principal) {
        return ResponseEntity.ok(ApiResponse.ok(
                paiementService.lister(eleveId, statut, operateur, dateDebut, dateFin, search, pageable, principal)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaiementResponseDTO>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal Utilisateur principal) {
        return ResponseEntity.ok(ApiResponse.ok(paiementService.getById(id, principal)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaiementResponseDTO>> modifier(
            @PathVariable Long id,
            @RequestBody ModifierPaiementRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(paiementService.modifier(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> supprimer(@PathVariable Long id) {
        paiementService.supprimer(id);
        return ResponseEntity.ok(ApiResponse.ok("Transaction supprimée"));
    }
}
