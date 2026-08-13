package com.klem.billing.application.port;

public record RefundResult(String refundReference, boolean accepted) {
}
