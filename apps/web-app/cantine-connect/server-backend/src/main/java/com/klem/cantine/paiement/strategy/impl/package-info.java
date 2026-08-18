/**
 * Implémentations de {@link com.klem.cantine.paiement.strategy.PaymentStrategy}.
 * <p>
 * <b>Extensibilité (principe Ouvert/Fermé) :</b> ajouter {@code MtnMoMoPaymentStrategy} ou
 * {@code WavePaymentStrategy} ne nécessite de modifier aucune ligne du code existant — ni
 * {@code PaymentStrategy}, ni {@code PaymentStrategyFactory}, ni {@code CanteenPaymentServiceImpl}.
 * Il suffit de :
 * <pre>
 * {@literal @}Component
 * {@literal @}RequiredArgsConstructor
 * public class MtnMoMoPaymentStrategy implements PaymentStrategy {
 *
 *     {@literal @}Override
 *     public PaymentProviderType getProviderType() { return PaymentProviderType.MTN_MOMO_CI; }
 *
 *     {@literal @}Override
 *     public PaymentResponseDto initiatePayment(PaymentRequestDto request) { ... }
 *
 *     {@literal @}Override
 *     public PaymentResponseDto handleWebhook(WebhookPayloadDto webhookPayload) { ... }
 *
 *     {@literal @}Override
 *     public PaymentResponseDto checkTransactionStatus(String transactionReference) { ... }
 *
 *     {@literal @}Override
 *     public boolean validateWebhookSignature(WebhookPayloadDto webhookPayload) { ... }
 * }
 * </pre>
 * Spring déclare le bean, {@code PaymentStrategyFactory} l'indexe automatiquement par
 * {@code getProviderType()} au démarrage (voir son {@code @PostConstruct}), et
 * {@code klem.payment.default-provider} peut être basculé vers {@code MTN_MOMO_CI} dès que le
 * fournisseur est prêt — sans redéploiement des autres stratégies. {@code WavePaymentStrategy}
 * s'insère de façon identique pour {@code PaymentProviderType.WAVE_CI}.
 */
package com.klem.cantine.paiement.strategy.impl;
