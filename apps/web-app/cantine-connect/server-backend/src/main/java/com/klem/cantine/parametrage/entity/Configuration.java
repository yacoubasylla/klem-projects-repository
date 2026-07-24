package com.klem.cantine.parametrage.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "configurations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cle", unique = true, nullable = false, length = 100)
    private String cle;

    @Column(name = "valeur", nullable = false)
    private String valeur;

    @Column(name = "description")
    private String description;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @PreUpdate
    @PrePersist
    public void touch() {
        this.dateModification = LocalDateTime.now();
    }
}
