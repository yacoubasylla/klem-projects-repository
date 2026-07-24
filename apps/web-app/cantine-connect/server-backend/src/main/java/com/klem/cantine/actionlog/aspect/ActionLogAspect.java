package com.klem.cantine.actionlog.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klem.cantine.actionlog.annotation.Traceable;
import com.klem.cantine.actionlog.entity.TypeAction;
import com.klem.cantine.actionlog.service.ActionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ActionLogAspect {

    private final ActionLogService actionLogService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(traceable)")
    public Object logAction(ProceedingJoinPoint pjp, Traceable traceable) throws Throwable {
        String payloadAvant = captureAvant(traceable.action(), pjp.getArgs());

        Object result = pjp.proceed();

        String payloadApres = captureApres(traceable.action(), result);
        String entiteId    = extraireEntiteId(traceable.action(), pjp.getArgs(), result);
        String auteur      = extraireAuteur();

        actionLogService.sauvegarder(auteur, traceable.action(), traceable.entite(),
                entiteId, payloadAvant, payloadApres);

        return result;
    }

    // ── Capture du payload "avant" ───────────────────────────────────────────

    private String captureAvant(TypeAction action, Object[] args) {
        return switch (action) {
            case CREATE -> null;
            case UPDATE -> serialize(args.length > 1 ? args[1] : null);
            case DELETE -> serialize(args.length > 0
                    ? Map.of("id", String.valueOf(args[0]))
                    : null);
        };
    }

    // ── Capture du payload "après" ───────────────────────────────────────────

    private String captureApres(TypeAction action, Object result) {
        return switch (action) {
            case CREATE, UPDATE -> serialize(result);
            case DELETE -> null;
        };
    }

    // ── Extraction de l'ID de l'entité concernée ─────────────────────────────

    private String extraireEntiteId(TypeAction action, Object[] args, Object result) {
        // CREATE : l'ID est dans la valeur de retour (record avec accesseur id())
        if (action == TypeAction.CREATE && result != null) {
            try {
                Object id = result.getClass().getMethod("id").invoke(result);
                return id != null ? String.valueOf(id) : "unknown";
            } catch (Exception ignored) {}
        }
        // UPDATE / DELETE : le premier argument Long est l'ID
        if (args.length > 0 && args[0] instanceof Long id) {
            return String.valueOf(id);
        }
        return "unknown";
    }

    // ── Extraction de l'auteur depuis le SecurityContext ─────────────────────

    private String extraireAuteur() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return (auth != null && auth.isAuthenticated()) ? auth.getName() : "system";
        } catch (Exception e) {
            return "system";
        }
    }

    // ── Sérialisation JSON silencieuse ────────────────────────────────────────

    private String serialize(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("ActionLogAspect : sérialisation impossible pour {}", obj.getClass().getSimpleName());
            return obj.toString();
        }
    }
}
