package com.klem.cantine.etablissement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "niveaux")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Niveau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etablissement_id", nullable = false)
    private Etablissement etablissement;

    @Column(nullable = false, length = 50)
    private String libelle;

    @Column(nullable = false)
    private Integer ordre = 0;

    @OneToMany(mappedBy = "niveau", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Classe> classes = new ArrayList<>();
}
