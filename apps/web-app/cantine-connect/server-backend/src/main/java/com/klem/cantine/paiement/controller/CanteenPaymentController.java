package com.klem.cantine.paiement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klem.cantine.common.ApiResponse;
import com.klem.cantine.paiement.service.CanteenPaymentService;
import com.klem.cantine.paiement.strategy.dto.PaymentRequestDto;
import com.klem.cantine.paiement.strategy.dto.PaymentResponseDto;
import com.klem.cantine.paiement.strategy.dto.WebhookPayloadDto;
import com.klem.cantine.paiement.strategy.enums.PaymentProviderType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Point d'entrée REST du paiement multi-providers (contrat unifié {@code PaymentStrategy}).
 * Coexiste avec {@code /api/v1/paiements}/{@code PaiementController}, laissé inchangé.
 */
@RestController
@RequestMapping("/api/v2/canteen-payments")
@RequiredArgsConstructor
@Slf4j
public class CanteenPaymentController {

    private final CanteenPaymentService canteenPaymentService;
    private final ObjectMapper objectMapper;

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> initiate(@Valid @RequestBody PaymentRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(canteenPaymentService.initiatePayment(request)));
    }

    @GetMapping("/{provider}/{transactionReference}/status")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> status(
            @PathVariable PaymentProviderType provider,
            @PathVariable String transactionReference) {
        return ResponseEntity.ok(ApiResponse.ok(
                canteenPaymentService.checkTransactionStatus(provider, transactionReference)));
    }

    /**
     * Callback IPN générique — appelé par le fournisseur, sans JWT. Retourne 200 immédiatement
     * une fois la signature validée et le résultat appliqué à la transaction.
     */
    @PostMapping("/webhooks/{provider}")
    public ResponseEntity<Void> webhook(
            @PathVariable PaymentProviderType provider,
            @RequestBody Map<String, Object> fields,
            @RequestHeader Map<String, String> headers) {
        log.info("Webhook {} reçu (contrat unifié)", provider);
        WebhookPayloadDto payload = WebhookPayloadDto.builder()
                .provider(provider)
                .rawBody(toJson(fields))
                .fields(fields)
                .signatureHeaders(headers)
                .build();
        canteenPaymentService.confirmWebhook(provider, payload);
        return ResponseEntity.ok().build();
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }
}
