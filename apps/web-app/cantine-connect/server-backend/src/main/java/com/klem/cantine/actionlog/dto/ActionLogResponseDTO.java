package com.klem.cantine.actionlog.dto;

import com.klem.cantine.actionlog.entity.ActionLog;
import com.klem.cantine.actionlog.entity.TypeAction;

import java.time.LocalDateTime;

public record ActionLogResponseDTO(
        Long id,
        String auteur,
        TypeAction typeAction,
        String entite,
        String entiteId,
        String payloadAvant,
        String payloadApres,
        LocalDateTime dateAction
) {
    public static ActionLogResponseDTO from(ActionLog log) {
        return new ActionLogResponseDTO(
                log.getId(),
                log.getAuteur(),
                log.getTypeAction(),
                log.getEntite(),
                log.getEntiteId(),
                log.getPayloadAvant(),
                log.getPayloadApres(),
                log.getDateAction()
        );
    }
}
