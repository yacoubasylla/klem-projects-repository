package com.klem.coreapi.referential.api.controller;

import com.klem.coreapi.referential.api.response.ReferentialListResponse;
import com.klem.coreapi.referential.application.service.ReferentialService;
import com.klem.coreapi.referential.domain.model.ReferentialEntry;
import com.klem.coreapi.referential.domain.model.ReferentialList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Aucune restriction de rôle : un référentiel commun (pays, devises) est une donnée de faible
 * sensibilité, utile à tout utilisateur authentifié (listes déroulantes, etc.) — à la différence
 * des endpoints {@code tenant}/{@code identity}, réservés à {@code PLATFORM_ADMIN}.
 */
@RestController
@RequestMapping("/api/v1/referentials")
public class ReferentialController {

    private final ReferentialService referentialService;

    public ReferentialController(ReferentialService referentialService) {
        this.referentialService = referentialService;
    }

    @GetMapping("/{code}")
    public ReferentialListResponse get(@PathVariable String code) {
        ReferentialList list = referentialService.getList(code);
        List<ReferentialEntry> entries = referentialService.getEntries(list.getId());
        return ReferentialListResponse.from(list, entries);
    }
}
