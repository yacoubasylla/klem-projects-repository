package com.klem.cantine.eleve.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatriculeGeneratorTest {

    @Mock private JdbcTemplate jdbcTemplate;

    @Test
    void genererMatricule_formateAvecAnneeEtRangSurQuatreChiffres() {
        MatriculeGenerator generator = new MatriculeGenerator(jdbcTemplate);
        int annee = Year.now().getValue();
        when(jdbcTemplate.queryForObject(any(String.class), eq(Integer.class), eq(annee))).thenReturn(1);

        String matricule = generator.genererMatricule();

        assertThat(matricule).isEqualTo("E" + annee + "0001");
    }

    @Test
    void genererMatricule_padLeRangSurQuatreChiffres_memePourUnRangEleve() {
        MatriculeGenerator generator = new MatriculeGenerator(jdbcTemplate);
        int annee = Year.now().getValue();
        when(jdbcTemplate.queryForObject(any(String.class), eq(Integer.class), eq(annee))).thenReturn(1234);

        String matricule = generator.genererMatricule();

        assertThat(matricule).isEqualTo("E" + annee + "1234");
    }
}
