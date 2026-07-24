package com.klem.cantine.actionlog.service;

import com.klem.cantine.actionlog.dto.ActionLogResponseDTO;
import com.klem.cantine.actionlog.entity.ActionLog;
import com.klem.cantine.actionlog.entity.TypeAction;
import com.klem.cantine.actionlog.repository.ActionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;

    @Async
    public void sauvegarder(String auteur, TypeAction typeAction, String entite,
                             String entiteId, String payloadAvant, String payloadApres) {
        try {
            ActionLog entry = ActionLog.builder()
                    .auteur(auteur)
                    .typeAction(typeAction)
                    .entite(entite)
                    .entiteId(entiteId)
                    .payloadAvant(payloadAvant)
                    .payloadApres(payloadApres)
                    .dateAction(LocalDateTime.now())
                    .build();
            actionLogRepository.save(entry);
        } catch (Exception e) {
            log.error("ActionLog : échec de la sauvegarde [entite={}, action={}]", entite, typeAction, e);
        }
    }

    @Transactional(readOnly = true)
    public Page<ActionLogResponseDTO> lister(String entite, String entiteId,
                                              String auteur, Pageable pageable) {
        Page<ActionLog> page;
        if (entite != null && entiteId != null) {
            page = actionLogRepository.findByEntiteAndEntiteId(entite, entiteId, pageable);
        } else if (entite != null) {
            page = actionLogRepository.findByEntite(entite, pageable);
        } else if (auteur != null) {
            page = actionLogRepository.findByAuteur(auteur, pageable);
        } else {
            page = actionLogRepository.findAll(pageable);
        }
        return page.map(ActionLogResponseDTO::from);
    }
}
