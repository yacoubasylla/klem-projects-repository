package com.klem.cantine.paiement.strategy;

import com.klem.cantine.paiement.strategy.dto.PaymentRequestDto;
import com.klem.cantine.paiement.strategy.dto.PaymentResponseDto;
import com.klem.cantine.paiement.strategy.dto.WebhookPayloadDto;
import com.klem.cantine.paiement.strategy.enums.PaymentProviderType;

/**
 * Contrat unifié d'un fournisseur de paiement Mobile Money/agrégateur.
 * <p>
 * Chaque implémentation est un bean Spring (une seule instance par {@link PaymentProviderType}) :
 * {@link com.klem.cantine.paiement.service.PaymentStrategyFactory} les collecte automatiquement
 * pour router une demande vers la bonne stratégie sans jamais faire de branchement conditionnel
 * dans le service métier (principe Ouvert/Fermé — ajouter un fournisseur = ajouter une classe,
 * jamais modifier une classe existante).
 * <p>
 * Les fournisseurs déjà intégrés avant l'introduction de ce contrat (CinetPay) l'implémentent en
 * plus de leur interface historique {@link com.klem.cantine.paiement.provider.PaymentProvider},
 * sans que leur comportement existant ne soit modifié — voir {@code CinetPayProvider}.
 */
public interface PaymentStrategy {

    /** Fournisseur géré par cette stratégie — sert de clé dans {@code PaymentStrategyFactory}. */
    PaymentProviderType getProviderType();

    /**
     * Initie la demande de paiement auprès du fournisseur et renvoie l'URL de redirection
     * (ou l'identifiant de transaction pour une intégration marchande directe sans redirection).
     */
    PaymentResponseDto initiatePayment(PaymentRequestDto request);

    /**
     * Traduit le callback asynchrone (webhook/IPN) du fournisseur en réponse unifiée.
     * Ne persiste rien : la mise à jour de la transaction et des effets métier (statut élève,
     * crédit de solde) restent la responsabilité de l'appelant
     * ({@code CanteenPaymentServiceImpl}), une fois {@link #validateWebhookSignature} vérifié.
     */
    PaymentResponseDto handleWebhook(WebhookPayloadDto webhookPayload);

    /** Interroge l'API du fournisseur pour connaître l'état réel d'une transaction déjà initiée. */
    PaymentResponseDto checkTransactionStatus(String transactionReference);

    /**
     * Vérifie l'authenticité du webhook (signature HMAC/SHA256 ou équivalent propre au
     * fournisseur) avant tout traitement. Doit être appelée avant {@link #handleWebhook}.
     */
    boolean validateWebhookSignature(WebhookPayloadDto webhookPayload);
}
