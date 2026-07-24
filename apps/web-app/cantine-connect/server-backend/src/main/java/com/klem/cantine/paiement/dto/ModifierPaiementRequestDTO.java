package com.klem.cantine.paiement.dto;

import com.klem.cantine.paiement.entity.OperateurMobileMoney;
import com.klem.cantine.paiement.entity.StatutPaiement;

import java.math.BigDecimal;

public record ModifierPaiementRequestDTO(
        StatutPaiement statut,
        BigDecimal montant,
        OperateurMobileMoney operateur,
        String telephonePayeur
) {}
