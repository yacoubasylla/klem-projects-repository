package com.klem.cantine.paiement.dto;

import com.klem.cantine.paiement.entity.OperateurMobileMoney;
import com.klem.cantine.paiement.entity.StatutPaiement;
import com.klem.cantine.paiement.entity.TransactionPaiement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaiementResponseDTO(
        Long id,
        Long eleveId,
        String eleveNomComplet,
        String referenceInterne,
        String referencePlateforme,
        OperateurMobileMoney operateur,
        BigDecimal montant,
        String devise,
        String telephonePayeur,
        StatutPaiement statut,
        String paymentUrl,
        LocalDateTime dateCreation,
        LocalDateTime dateMiseAJour
) {
    public static PaiementResponseDTO from(TransactionPaiement t) {
        return from(t, null);
    }

    public static PaiementResponseDTO from(TransactionPaiement t, String paymentUrl) {
        String nomComplet = t.getEleve().getPrenom() + " " + t.getEleve().getNom();
        return new PaiementResponseDTO(
                t.getId(),
                t.getEleve().getId(),
                nomComplet,
                t.getReferenceInterne(),
                t.getReferencePlateforme(),
                t.getOperateur(),
                t.getMontant(),
                t.getDevise(),
                t.getTelephonePayeur(),
                t.getStatut(),
                paymentUrl,
                t.getDateCreation(),
                t.getDateMiseAJour()
        );
    }
}
