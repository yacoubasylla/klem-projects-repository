package com.klem.cantine.paiement.strategy.dto;

import com.klem.cantine.paiement.strategy.enums.PaymentProviderType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * Demande de paiement unifiée, indépendante du fournisseur choisi.
 * {@code orderId} sert de référence interne transmise telle quelle au fournisseur
 * (ex. {@code transaction_id} CinetPay) : elle doit donc être unique par tentative de paiement.
 */
@Builder
public record PaymentRequestDto(

        @NotNull(message = "L'identifiant de l'élève est obligatoire")
        Long studentId,

        @NotBlank(message = "La référence de commande est obligatoire")
        String orderId,

        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "100.0", message = "Le montant minimum est 100 XOF")
        BigDecimal amount,

        @NotBlank(message = "Le numéro de téléphone du payeur est obligatoire")
        String customerPhoneNumber,

        /** Provider choisi par l'utilisateur ; si absent, {@link com.klem.cantine.paiement.service.PaymentStrategyFactory} retombe sur le provider par défaut. */
        PaymentProviderType provider,

        String description,

        String returnUrl,

        String cancelUrl
) {}
