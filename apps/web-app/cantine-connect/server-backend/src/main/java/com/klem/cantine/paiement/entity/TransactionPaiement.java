package com.klem.cantine.paiement.entity;

import com.klem.cantine.eleve.entity.Eleve;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions_paiement", indexes = {
    @Index(name = "idx_tp_eleve",        columnList = "eleve_id"),
    @Index(name = "idx_tp_reference",    columnList = "reference_interne"),
    @Index(name = "idx_tp_operator_tx",  columnList = "operator_tx_id"),
    @Index(name = "idx_tp_statut",       columnList = "statut")
})
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class TransactionPaiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eleve_id", nullable = false)
    private Eleve eleve;

    @Column(name = "reference_interne", unique = true, nullable = false, length = 64)
    private String referenceInterne;

    @Column(name = "reference_plateforme", length = 128)
    private String referencePlateforme;

    @Column(name = "operator_tx_id", length = 128)
    private String operatorTxId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OperateurMobileMoney operateur;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Builder.Default
    @Column(nullable = false, length = 5)
    private String devise = "XOF";

    @Column(name = "telephone_payeur", length = 20)
    private String telephonePayeur;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutPaiement statut = StatutPaiement.EN_ATTENTE;

    @Builder.Default
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "date_mise_a_jour")
    private LocalDateTime dateMiseAJour;

    @Column(name = "metadonnees_webhook", columnDefinition = "TEXT")
    private String metadonneesWebhook;
}
