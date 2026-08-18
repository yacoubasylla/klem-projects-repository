package com.klem.cantine.paiement.strategy.dto;

import com.klem.cantine.paiement.strategy.enums.PaymentProviderType;
import lombok.Builder;

import java.util.Map;

/**
 * Enveloppe générique d'un callback (webhook/IPN) reçu d'un fournisseur, avant tout
 * traitement métier. {@code rawBody} est conservé tel quel (JSON brut) pour permettre à
 * {@link com.klem.cantine.paiement.strategy.PaymentStrategy#validateWebhookSignature} de
 * recalculer la signature exactement comme le fournisseur l'a produite.
 */
@Builder
public record WebhookPayloadDto(

        PaymentProviderType provider,

        String rawBody,

        /** Champs déjà désérialisés du payload (ex. {@code cpm_trans_id}, {@code signature}). */
        Map<String, Object> fields,

        /** En-têtes HTTP porteurs de la signature (ex. {@code X-Signature}), selon le fournisseur. */
        Map<String, String> signatureHeaders
) {}
