package com.klem.cantine.paiement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klem.cantine.paiement.dto.WebhookCinetPayDTO;
import com.klem.cantine.paiement.dto.WebhookPayDunyaDTO;
import com.klem.cantine.paiement.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final WebhookService webhookService;
    private final ObjectMapper objectMapper;

    /**
     * Endpoint IPN CinetPay — appelé par la plateforme de paiement, sans JWT.
     * Retourne 200 immédiatement ; traitement asynchrone (@Async).
     */
    @PostMapping("/cinetpay")
    public ResponseEntity<Void> cinetpay(@RequestBody WebhookCinetPayDTO dto) {
        log.info("Webhook CinetPay reçu : transId={} statut={}", dto.cpmTransId(), dto.cpmTransStatus());
        String rawBody = toJson(dto);
        webhookService.traiterCinetPay(dto, rawBody);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint IPN PayDunya — même principe.
     */
    @PostMapping("/paydunya")
    public ResponseEntity<Void> paydunya(@RequestBody WebhookPayDunyaDTO dto) {
        log.info("Webhook PayDunya reçu : token={} statut={}", dto.token(), dto.status());
        String rawBody = toJson(dto);
        webhookService.traiterPayDunya(dto, rawBody);
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
