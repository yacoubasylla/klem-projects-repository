package com.klem.cantine.scan.dto;

import com.klem.cantine.scan.entity.MotifRefus;
import com.klem.cantine.scan.entity.PassageRefectoire;
import com.klem.cantine.scan.entity.ResultatScan;

import java.time.LocalDateTime;

public record ScanResultDTO(
        String acces,             // "ACCORDÉ" ou "REFUSÉ"
        ResultatScan resultat,
        MotifRefus motifRefus,    // null si ACCORDÉ
        Long eleveId,
        String nomComplet,
        String matricule,
        String classeNom,
        String etablissementNom,
        Long passageId,
        LocalDateTime heurePassage
) {
    public static ScanResultDTO from(PassageRefectoire p) {
        var eleve  = p.getEleve();
        String acces = p.getResultat() == ResultatScan.ACCORDE ? "ACCORDÉ" : "REFUSÉ";
        return new ScanResultDTO(
                acces,
                p.getResultat(),
                p.getMotifRefus(),
                eleve.getId(),
                eleve.getPrenom() + " " + eleve.getNom(),
                eleve.getMatricule(),
                eleve.getClasse().getLibelle(),
                eleve.getEtablissement().getNom(),
                p.getId(),
                p.getHeurePassage()
        );
    }
}
