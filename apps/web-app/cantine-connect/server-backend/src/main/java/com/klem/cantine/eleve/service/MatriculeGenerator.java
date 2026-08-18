package com.klem.cantine.eleve.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Year;

/**
 * Génère le matricule élève — identifiant unique et définitif attribué par l'application
 * (Cantine Connect n'a pas de matricule scolaire officiel à faire coïncider), format
 * {@code E<ANNEE><RANG sur 4 chiffres>} (ex. {@code E20260001}).
 * <p>
 * Thread-safe et sûr en environnement multi-instance : l'incrémentation du rang repose sur un
 * {@code INSERT ... ON CONFLICT DO UPDATE ... RETURNING} PostgreSQL, une seule instruction
 * atomique (verrouillage de ligne géré par la base), sans synchronisation applicative.
 */
@Component
public class MatriculeGenerator {

    private final JdbcTemplate jdbcTemplate;

    public MatriculeGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String genererMatricule() {
        int annee = Year.now().getValue();
        Integer rang = jdbcTemplate.queryForObject("""
                INSERT INTO matricule_sequences (annee, dernier_rang) VALUES (?, 1)
                ON CONFLICT (annee) DO UPDATE SET dernier_rang = matricule_sequences.dernier_rang + 1
                RETURNING dernier_rang
                """, Integer.class, annee);
        return "E" + annee + String.format("%04d", rang);
    }
}
