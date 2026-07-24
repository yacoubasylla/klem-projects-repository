package com.klem.cantine.etablissement.dto;

import com.klem.cantine.etablissement.entity.Etablissement;

public record EtablissementResponseDTO(Long id, String nom, String adresse, String ville, String telephone) {

    public static EtablissementResponseDTO from(Etablissement e) {
        return new EtablissementResponseDTO(e.getId(), e.getNom(), e.getAdresse(), e.getVille(), e.getTelephone());
    }
}
