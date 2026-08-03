package com.klem.cantine.eleve.entity;

import com.klem.cantine.etablissement.entity.Classe;
import com.klem.cantine.etablissement.entity.Etablissement;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "eleves", indexes = {
    @Index(name = "idx_eleves_etablissement", columnList = "etablissement_id"),
    @Index(name = "idx_eleves_classe", columnList = "classe_id"),
    @Index(name = "idx_eleves_qr_code_token", columnList = "qr_code_token"),
    @Index(name = "idx_eleves_statut_acces", columnList = "statut_acces")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Eleve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etablissement_id", nullable = false)
    private Etablissement etablissement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    @Column(nullable = false, unique = true, length = 30)
    private String matricule;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    // length=1 pousse Hibernate 6 à valider le schéma en attendant un type SQL
    // CHAR(1) plutôt que VARCHAR — élargi pour rester sur un VARCHAR standard.
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Sexe sexe;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    // ── Lieu d'habitation de l'élève ──────────────────────
    @Column(length = 100)
    private String ville;

    @Column(length = 100)
    private String commune;

    @Column(length = 100)
    private String quartier;

    // ── Cantine / Affectation ─────────────────────────────
    @Column(name = "qr_code_token", nullable = false, unique = true)
    private UUID qrCodeToken;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_acces", nullable = false, length = 30)
    private StatutAcces statutAcces = StatutAcces.EN_ATTENTE_PAIEMENT;

    @Column(name = "date_fin_grace")
    private LocalDate dateFinGrace;

    @Builder.Default
    @Column(name = "est_boursier", nullable = false)
    private Boolean estBoursier = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "regime_alimentaire", nullable = false, length = 30)
    private RegimeAlimentaire regimeAlimentaire = RegimeAlimentaire.STANDARD;

    // ── Contacts / Allergies ──────────────────────────────
    @Column(name = "parent_nom", nullable = false, length = 150)
    private String parentNom;

    @Column(name = "parent_telephone", nullable = false, length = 20)
    private String parentTelephone;

    @Column(name = "parent_email", length = 100)
    private String parentEmail;

    @Column(columnDefinition = "TEXT")
    private String allergies;

    // Obligatoire dès lors qu'une allergie est déclarée (validée par le service,
    // pas de contrainte SQL, pour un message métier clair) — certificat médical
    // d'un allergologue justifiant l'allergie.
    @Column(name = "certificat_medical_url")
    private String certificatMedicalUrl;

    @Column(name = "notes_medicales", columnDefinition = "TEXT")
    private String notesMedicales;

    // Périodicité de l'abonnement cantine (mode ABONNEMENT) — trimestriel ou annuel
    // uniquement, sans objet en mode CREDITS.
    @Enumerated(EnumType.STRING)
    @Column(name = "periode_abonnement", length = 20)
    private PeriodeAbonnement periodeAbonnement;

    // ── Solde (mode CREDITS) ──────────────────────────────
    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal solde = java.math.BigDecimal.ZERO;

    // ── Métadonnées ───────────────────────────────────────
    @Builder.Default
    @Column(nullable = false)
    private Boolean actif = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (qrCodeToken == null) {
            qrCodeToken = UUID.randomUUID();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
