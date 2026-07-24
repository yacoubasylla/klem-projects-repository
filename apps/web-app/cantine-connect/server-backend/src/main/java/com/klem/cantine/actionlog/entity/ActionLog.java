package com.klem.cantine.actionlog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "action_logs", indexes = {
    @Index(name = "idx_al_entite_id", columnList = "entite, entite_id"),
    @Index(name = "idx_al_auteur",    columnList = "auteur"),
    @Index(name = "idx_al_date",      columnList = "date_action")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String auteur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAction typeAction;

    @Column(nullable = false)
    private String entite;

    @Column(name = "entite_id")
    private String entiteId;

    @Column(columnDefinition = "TEXT")
    private String payloadAvant;

    @Column(columnDefinition = "TEXT")
    private String payloadApres;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dateAction = LocalDateTime.now();
}
