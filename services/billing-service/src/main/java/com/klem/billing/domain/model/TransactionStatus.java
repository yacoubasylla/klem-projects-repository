package com.klem.billing.domain.model;

import java.util.Set;

/**
 * Machine à états — voir shared_architecture/billing_&_payments/specifications_techniques.md §5.
 * Une transaction ne quitte jamais CONFIRMED autrement que via REFUND_INITIATED : pas de
 * transition directe CONFIRMED → FAILED.
 */
public enum TransactionStatus {

    PENDING {
        @Override
        public Set<TransactionStatus> allowedNextStates() {
            return Set.of(CONFIRMED, FAILED);
        }
    },
    CONFIRMED {
        @Override
        public Set<TransactionStatus> allowedNextStates() {
            return Set.of(REFUND_INITIATED);
        }
    },
    FAILED {
        @Override
        public Set<TransactionStatus> allowedNextStates() {
            return Set.of();
        }
    },
    REFUND_INITIATED {
        @Override
        public Set<TransactionStatus> allowedNextStates() {
            return Set.of(REFUNDED);
        }
    },
    REFUNDED {
        @Override
        public Set<TransactionStatus> allowedNextStates() {
            return Set.of();
        }
    };

    public abstract Set<TransactionStatus> allowedNextStates();

    public boolean canTransitionTo(TransactionStatus target) {
        return allowedNextStates().contains(target);
    }
}
