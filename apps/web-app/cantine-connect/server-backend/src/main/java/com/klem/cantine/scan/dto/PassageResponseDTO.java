package com.klem.cantine.scan.dto;

import com.klem.cantine.scan.entity.MotifRefus;
import com.klem.cantine.scan.entity.PassageRefectoire;
import com.klem.cantine.scan.entity.ResultatScan;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PassageResponseDTO(
        Long id,
        Long eleveId,
        String eleveNomComplet,
        String eleveMatricule,
        String classeNom,
        String etablissementNom,
        UUID qrCodeToken,
        LocalDate datePassage,
        LocalDateTime heurePassage,
        ResultatScan resultat,
        MotifRefus motifRefus
) {
    public static PassageResponseDTO from(PassageRefectoire p) {
        var eleve = p.getEleve();
        return new PassageResponseDTO(
                p.getId(),
                eleve.getId(),
                eleve.getPrenom() + " " + eleve.getNom(),
                eleve.getMatricule(),
                eleve.getClasse().getLibelle(),
                eleve.getEtablissement().getNom(),
                p.getQrCodeToken(),
                p.getDatePassage(),
                p.getHeurePassage(),
                p.getResultat(),
                p.getMotifRefus()
        );
    }
}
