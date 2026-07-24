package com.klem.cantine.parametrage.controller;

import com.klem.cantine.common.ApiResponse;
import com.klem.cantine.parametrage.dto.ConfigurationDTO;
import com.klem.cantine.parametrage.dto.ModifierConfigurationRequestDTO;
import com.klem.cantine.parametrage.service.ConfigurationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/configurations")
@RequiredArgsConstructor
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConfigurationDTO>>> lister() {
        return ResponseEntity.ok(ApiResponse.ok(configurationService.listerToutes()));
    }

    @GetMapping("/{cle}")
    public ResponseEntity<ApiResponse<ConfigurationDTO>> getParCle(@PathVariable String cle) {
        return ResponseEntity.ok(ApiResponse.ok(configurationService.getParCle(cle)));
    }

    @PutMapping("/{cle}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ConfigurationDTO>> modifier(
            @PathVariable String cle,
            @Valid @RequestBody ModifierConfigurationRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(configurationService.modifier(cle, dto.valeur())));
    }
}
