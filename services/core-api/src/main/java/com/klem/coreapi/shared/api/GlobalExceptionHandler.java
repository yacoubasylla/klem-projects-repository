package com.klem.coreapi.shared.api;

import com.klem.coreapi.shared.domain.BadRequestException;
import com.klem.coreapi.shared.domain.ConflictException;
import com.klem.coreapi.shared.domain.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.UUID;

/**
 * Traduit toute exception en {@link ApiError} homogène — jamais de stack trace ni de message
 * d'exception brut renvoyé au client ({@code KLEM_MASTER_SYSTEM_DIRECTIVE.md} §6).
 * <p>
 * {@code requestId} est généré ici faute de filtre de corrélation dédié à ce stade du Sprint —
 * à remplacer par une valeur propagée depuis un en-tête {@code X-Request-Id}/le MDC de logging une
 * fois ce filtre introduit (voir {@code knowledge/10-production-reliability.md}, observabilité).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        String requestId = newRequestId();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", ex.getMessage(), requestId));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        String requestId = newRequestId();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST", ex.getMessage(), requestId));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        String requestId = newRequestId();
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of(HttpStatus.CONFLICT.value(), "CONFLICT", ex.getMessage(), requestId));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        // Couvre AuthorizationDeniedException (@PreAuthorize) : sans ce handler explicite, le
        // handler générique ci-dessous intercepterait l'exception avant qu'ExceptionTranslationFilter
        // ne puisse la traduire en 403 — bug constaté par test (TenantControllerWebMvcTest).
        String requestId = newRequestId();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiError.of(HttpStatus.FORBIDDEN.value(), "ACCESS_DENIED",
                        "Vous n'avez pas la permission d'effectuer cette action.", requestId));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String requestId = newRequestId();
        List<ApiErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toDetail)
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR",
                        "La requête est invalide.", details, requestId));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        String requestId = newRequestId();
        log.error("Erreur non gérée, requestId={}", requestId, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_ERROR",
                        "Une erreur inattendue est survenue.", requestId));
    }

    private ApiErrorDetail toDetail(FieldError fieldError) {
        return new ApiErrorDetail(fieldError.getField(), fieldError.getDefaultMessage());
    }

    private String newRequestId() {
        return "req_" + UUID.randomUUID();
    }
}
