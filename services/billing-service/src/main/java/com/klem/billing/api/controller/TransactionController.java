package com.klem.billing.api.controller;

import com.klem.billing.api.request.InitiateTransactionRequest;
import com.klem.billing.api.request.RefundRequest;
import com.klem.billing.api.response.TransactionResponse;
import com.klem.billing.application.service.TransactionService;
import com.klem.billing.domain.model.Transaction;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** Contrat API — spécifications_techniques.md §6. Authentification service-à-service via IAM (§4). */
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> initiate(@Valid @RequestBody InitiateTransactionRequest request,
                                                          @RequestHeader("Idempotency-Key") String idempotencyKey) {
        Transaction transaction = request.aggregator() != null
                ? transactionService.initiateViaAggregator(request.tenantId(), idempotencyKey, request.operator(),
                        request.aggregator(), request.amount(), request.currency(), request.payerReference())
                : transactionService.initiateViaDirectApi(request.tenantId(), idempotencyKey, request.operator(),
                        request.amount(), request.currency(), request.payerReference());

        return ResponseEntity.ok(TransactionResponse.from(transaction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(TransactionResponse.from(transactionService.findById(id)));
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<TransactionResponse> refund(@PathVariable UUID id, @RequestBody RefundRequest request) {
        Transaction transaction = transactionService.refund(id, request.reason());
        return ResponseEntity.ok(TransactionResponse.from(transaction));
    }
}
