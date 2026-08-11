package com.klem.referentielapi.shared.api;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Enveloppe de pagination standard pour toute API {@code referentiel-api-service} qui renvoie une
 * liste (`KLEM_MASTER_SYSTEM_DIRECTIVE.md` §6 : « pagination pour les listes »).
 */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
