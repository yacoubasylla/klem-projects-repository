package com.klem.cantine.parametrage.dto;

import com.klem.cantine.parametrage.entity.DemandeAcces;
import com.klem.cantine.parametrage.entity.StatutDemande;
import java.time.LocalDateTime;

public record DemandeAccesResponseDTO(
    Long id,
    String nom,
    String prenom,
    String fonction,
    String telephonePrincipal,
    String telephoneWhatsapp,
    String telephoneSecondaire,
    String email,
    String ville,
    String commune,
    String quartier,
    StatutDemande statut,
    LocalDateTime dateSoumission
) {
    public static DemandeAccesResponseDTO from(DemandeAcces d) {
        return new DemandeAccesResponseDTO(
            d.getId(), d.getNom(), d.getPrenom(), d.getFonction(),
            d.getTelephonePrincipal(), d.getTelephoneWhatsapp(), d.getTelephoneSecondaire(),
            d.getEmail(), d.getVille(), d.getCommune(), d.getQuartier(),
            d.getStatut(), d.getDateSoumission()
        );
    }
}
