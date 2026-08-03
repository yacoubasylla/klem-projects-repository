package com.klem.cantine.etablissement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "etablissements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Etablissement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(length = 255)
    private String adresse;

    @Builder.Default
    @Column(nullable = false, length = 100)
    private String ville = "Abidjan";

    @Column(length = 20)
    private String telephone;

    // Délai de grâce (jours) accordé après échéance de paiement — surcharge la
    // valeur globale DELAI_GRACE_JOURS_DEFAUT si renseigné, sinon celle-ci s'applique.
    @Column(name = "delai_grace_jours")
    private Integer delaiGraceJours;

    @Builder.Default
    @Column(nullable = false)
    private Boolean actif = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "etablissement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Niveau> niveaux = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
