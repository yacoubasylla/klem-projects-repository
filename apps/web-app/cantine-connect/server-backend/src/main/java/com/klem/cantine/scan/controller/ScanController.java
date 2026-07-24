package com.klem.cantine.scan.controller;

import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.common.ApiResponse;
import com.klem.cantine.scan.dto.CacheEntreeDTO;
import com.klem.cantine.scan.dto.ModifierPassageRequestDTO;
import com.klem.cantine.scan.dto.PassageResponseDTO;
import com.klem.cantine.scan.dto.ScanResultDTO;
import com.klem.cantine.scan.entity.ResultatScan;
import com.klem.cantine.scan.service.ScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScanController {

    private final ScanService scanService;

    /**
     * Scan d'un QR Code — réponse < 1s.
     * POST /api/v1/scan/{qrCodeToken}
     */
    @PostMapping("/api/v1/scan/{qrCodeToken}")
    @PreAuthorize("!hasRole('PARENT')")
    public ResponseEntity<ApiResponse<ScanResultDTO>> scanner(
            @PathVariable String qrCodeToken) {
        ScanResultDTO result = scanService.scanner(qrCodeToken);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /**
     * Cache offline — liste complète des élèves actifs pour le mode dégradé 24h.
     * GET /api/v1/scan/cache
     */
    @GetMapping("/api/v1/scan/cache")
    @PreAuthorize("!hasRole('PARENT')")
    public ResponseEntity<ApiResponse<List<CacheEntreeDTO>>> cache() {
        List<CacheEntreeDTO> cache = scanService.getCacheOffline();
        return ResponseEntity.ok(ApiResponse.ok(
                "Cache généré — " + cache.size() + " élèves actifs", cache));
    }

    /**
     * Historique des passages — filtres multi-critères.
     * GET /api/v1/passages?date=2026-07-01&dateDebut=&dateFin=&etablissementId=&resultat=ACCORDE&search=
     * - date       : raccourci pour un seul jour (utilisé par ScanPage)
     * - dateDebut/dateFin : plage (prioritaire sur date)
     * - etablissementId, resultat, search : filtres optionnels
     */
    @GetMapping("/api/v1/passages")
    public ResponseEntity<ApiResponse<Page<PassageResponseDTO>>> passages(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Long etablissementId,
            @RequestParam(required = false) ResultatScan resultat,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 50, sort = "heurePassage") Pageable pageable,
            @AuthenticationPrincipal Utilisateur principal) {
        return ResponseEntity.ok(ApiResponse.ok(
                scanService.listerPassages(date, dateDebut, dateFin,
                        etablissementId, resultat, search, pageable, principal)));
    }

    /**
     * Modifier un passage (résultat + motif) — ADMIN uniquement.
     * PUT /api/v1/passages/{id}
     */
    @PutMapping("/api/v1/passages/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PassageResponseDTO>> modifierPassage(
            @PathVariable Long id,
            @RequestBody ModifierPassageRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(scanService.modifierPassage(id, dto)));
    }

    /**
     * Supprimer un passage — ADMIN uniquement.
     * DELETE /api/v1/passages/{id}
     */
    @DeleteMapping("/api/v1/passages/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> supprimerPassage(@PathVariable Long id) {
        scanService.supprimerPassage(id);
        return ResponseEntity.ok(ApiResponse.ok("Passage supprimé", null));
    }
}
