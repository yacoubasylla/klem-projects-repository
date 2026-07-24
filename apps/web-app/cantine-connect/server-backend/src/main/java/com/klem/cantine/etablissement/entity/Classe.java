package com.klem.cantine.etablissement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "classes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Classe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "niveau_id", nullable = false)
    private Niveau niveau;

    @Column(nullable = false, length = 50)
    private String libelle;

    @Column(name = "annee_scolaire", nullable = false, length = 10)
    private String anneeScolaire;
}
