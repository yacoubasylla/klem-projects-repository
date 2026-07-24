package com.klem.cantine.scan.dto;

import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.eleve.entity.StatutAcces;

public record CacheEntreeDTO(
        String qrCodeToken,
        Long eleveId,
        String nom,
        String prenom,
        String matricule,
        StatutAcces statutAcces,
        String classeNom,
        String etablissementNom
) {
    public static CacheEntreeDTO from(Eleve e) {
        return new CacheEntreeDTO(
                e.getQrCodeToken().toString(),
                e.getId(),
                e.getNom(),
                e.getPrenom(),
                e.getMatricule(),
                e.getStatutAcces(),
                e.getClasse().getLibelle(),
                e.getEtablissement().getNom()
        );
    }
}
