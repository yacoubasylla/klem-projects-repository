package com.klem.referentielapi.shared.api;

/** Un élément du tableau {@code details} d'une {@link ApiError} — typiquement une erreur de champ. */
public record ApiErrorDetail(String field, String reason) {
}
