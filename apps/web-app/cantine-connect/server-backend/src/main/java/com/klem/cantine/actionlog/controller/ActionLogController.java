package com.klem.cantine.actionlog.controller;

import com.klem.cantine.actionlog.dto.ActionLogResponseDTO;
import com.klem.cantine.actionlog.service.ActionLogService;
import com.klem.cantine.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class ActionLogController {

    private final ActionLogService actionLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ActionLogResponseDTO>>> lister(
            @RequestParam(required = false) String entite,
            @RequestParam(required = false) String entiteId,
            @RequestParam(required = false) String auteur,
            @PageableDefault(size = 20, sort = "dateAction") Pageable pageable) {

        Page<ActionLogResponseDTO> page = actionLogService.lister(entite, entiteId, auteur, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }
}
