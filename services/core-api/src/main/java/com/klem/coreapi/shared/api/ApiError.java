package com.klem.coreapi.shared.api;

import java.time.Instant;
import java.util.List;

/**
 * Format d'erreur homogène de toute API {@code core-api} — conforme à
 * {@code KLEM_MASTER_SYSTEM_DIRECTIVE.md} §6. Ne jamais renvoyer de stack trace ni d'identifiant
 * interne inutile au client : {@code message} reste un texte destiné à l'appelant, pas le message
 * brut d'une exception technique.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String code,
        String message,
        List<ApiErrorDetail> details,
        String requestId
) {

    public static ApiError of(int status, String code, String message, String requestId) {
        return new ApiError(Instant.now(), status, code, message, List.of(), requestId);
    }

    public static ApiError of(int status, String code, String message, List<ApiErrorDetail> details, String requestId) {
        return new ApiError(Instant.now(), status, code, message, details, requestId);
    }
}
