package com.klem.cantine.parametrage.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "demandes_acces", indexes = {
    @Index(name = "idx_da_telephone_principal", columnList = "telephone_principal"),
    @Index(name = "idx_da_email", columnList = "email"),
    @Index(name = "idx_da_statut", columnList = "statut")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DemandeAcces {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(length = 100)
    private String fonction;

    @Column(name = "telephone_principal", nullable = false, length = 20)
    private String telephonePrincipal;

    @Column(name = "telephone_whatsapp", length = 20)
    private String telephoneWhatsapp;

    @Column(name = "telephone_secondaire", length = 20)
    private String telephoneSecondaire;

    @Column(length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String ville;

    @Column(nullable = false, length = 100)
    private String commune;

    @Column(length = 100)
    private String quartier;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    @Column(name = "date_soumission", nullable = false, updatable = false)
    private LocalDateTime dateSoumission;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Column(name = "traite_par", length = 150)
    private String traitePar;

    @Column(name = "motif_rejet", columnDefinition = "TEXT")
    private String motifRejet;

    @PrePersist
    protected void onCreate() {
        dateSoumission = LocalDateTime.now();
    }
}
