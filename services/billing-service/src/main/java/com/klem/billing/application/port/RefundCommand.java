package com.klem.billing.application.port;

import java.math.BigDecimal;

public record RefundCommand(String operatorTxId, BigDecimal amount, String currency, String reason) {
}
