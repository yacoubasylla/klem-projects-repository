package com.klem.cantine.paiement.dto;

import com.klem.cantine.paiement.entity.OperateurMobileMoney;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record InitierPaiementRequestDTO(

        @NotNull(message = "L'identifiant de l'élève est obligatoire")
        Long eleveId,

        @NotNull(message = "L'opérateur Mobile Money est obligatoire")
        OperateurMobileMoney operateur,

        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "100.0", message = "Le montant minimum est 100 XOF")
        BigDecimal montant,

        @NotBlank(message = "Le numéro de téléphone du payeur est obligatoire")
        String telephonePayeur
) {}
