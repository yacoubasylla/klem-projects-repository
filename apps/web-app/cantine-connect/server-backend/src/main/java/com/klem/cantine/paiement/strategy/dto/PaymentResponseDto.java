package com.klem.cantine.paiement.strategy.dto;

import com.klem.cantine.paiement.strategy.enums.PaymentStatus;
import lombok.Builder;

import java.util.Map;

/**
 * Réponse unifiée renvoyée par une {@link com.klem.cantine.paiement.strategy.PaymentStrategy},
 * que ce soit à l'initiation, lors de la vérification de statut, ou après traitement d'un webhook.
 */
@Builder
public record PaymentResponseDto(

        /** Référence interne KLEM (= {@code PaymentRequestDto.orderId} pour la transaction concernée). */
        String transactionReference,

        /** Identifiant attribué par le fournisseur (ex. {@code cpm_payid} CinetPay), absent tant que non connu. */
        String providerTransactionId,

        /** URL de redirection du payeur ; absente pour une réponse de statut/webhook. */
        String paymentUrl,

        PaymentStatus status,

        /** Métadonnées brutes du fournisseur, conservées pour audit — jamais interprétées au-delà de ce contrat. */
        Map<String, Object> rawResponse
) {}
