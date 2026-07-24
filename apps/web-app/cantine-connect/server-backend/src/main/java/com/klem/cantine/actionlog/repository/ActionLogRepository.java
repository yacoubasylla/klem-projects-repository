package com.klem.cantine.actionlog.repository;

import com.klem.cantine.actionlog.entity.ActionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {

    Page<ActionLog> findByEntiteAndEntiteId(String entite, String entiteId, Pageable pageable);

    Page<ActionLog> findByEntite(String entite, Pageable pageable);

    Page<ActionLog> findByAuteur(String auteur, Pageable pageable);
}
