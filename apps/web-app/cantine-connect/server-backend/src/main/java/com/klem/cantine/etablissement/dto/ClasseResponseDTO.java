package com.klem.cantine.etablissement.dto;

import com.klem.cantine.etablissement.entity.Classe;

public record ClasseResponseDTO(Long id, Long niveauId, String libelle, String anneeScolaire) {

    public static ClasseResponseDTO from(Classe c) {
        return new ClasseResponseDTO(c.getId(), c.getNiveau().getId(), c.getLibelle(), c.getAnneeScolaire());
    }
}
