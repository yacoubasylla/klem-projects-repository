package com.klem.cantine.scan.entity;

import com.klem.cantine.eleve.entity.Eleve;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "passages_refectoire", indexes = {
    @Index(name = "idx_pr_eleve_date",  columnList = "eleve_id, date_passage"),
    @Index(name = "idx_pr_date",        columnList = "date_passage"),
    @Index(name = "idx_pr_qr_token",    columnList = "qr_code_token")
})
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PassageRefectoire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eleve_id", nullable = false)
    private Eleve eleve;

    // Dénormalisé pour l'audit — conservé même si l'élève est supprimé
    @Column(name = "qr_code_token", nullable = false)
    private UUID qrCodeToken;

    @Column(name = "date_passage", nullable = false)
    private LocalDate datePassage;

    @Column(name = "heure_passage", nullable = false)
    private LocalDateTime heurePassage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ResultatScan resultat;

    @Enumerated(EnumType.STRING)
    @Column(name = "motif_refus", length = 40)
    private MotifRefus motifRefus;
}
