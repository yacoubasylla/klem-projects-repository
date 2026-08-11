package com.klem.referentielapi.documentrequis.infrastructure.persistence;

import com.klem.referentielapi.documentrequis.application.port.DocumentRequisRepository;
import com.klem.referentielapi.documentrequis.domain.model.DocumentRequis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface DocumentRequisJpaRepository extends JpaRepository<DocumentRequis, UUID>, DocumentRequisRepository {
}
