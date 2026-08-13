package com.klem.billing.api.controller;

import com.klem.billing.application.port.WebhookCallback;
import com.klem.billing.application.service.TransactionService;
import com.klem.billing.domain.model.PaymentAggregator;
import com.klem.billing.domain.model.PaymentOperator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * POST /webhooks/{operator} — spécifications_techniques.md §6. `{operator}` accepte soit un
 * opérateur direct (WAVE, ORANGE_MONEY, MTN_MOBILE_MONEY, MOOV_MONEY) soit un agrégateur (CINETPAY,
 * BIZAO, FEDAPAY, PAYDUNYA) : les deux familles convergent vers le même {@link TransactionService},
 * seul le {@link com.klem.billing.application.port.PaymentProvider} résolu diffère.
 */
@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    private final TransactionService transactionService;

    public WebhookController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/{operatorOrAggregator}")
    public ResponseEntity<Void> receive(@PathVariable String operatorOrAggregator,
                                         @RequestBody String rawBody,
                                         HttpServletRequest request) {
        WebhookCallback callback = new WebhookCallback(rawBody, extractHeaders(request));

        PaymentOperator operator = parseEnum(PaymentOperator.class, operatorOrAggregator);
        PaymentAggregator aggregator = operator == null ? parseEnum(PaymentAggregator.class, operatorOrAggregator) : null;

        transactionService.handleWebhook(operator, aggregator, callback);
        return ResponseEntity.ok().build();
    }

    private Map<String, String> extractHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        var names = request.getHeaderNames();
        if (names == null) {
            return Collections.emptyMap();
        }
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            headers.put(name.toLowerCase(), request.getHeader(name));
        }
        return headers;
    }

    private <E extends Enum<E>> E parseEnum(Class<E> type, String value) {
        try {
            return Enum.valueOf(type, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
