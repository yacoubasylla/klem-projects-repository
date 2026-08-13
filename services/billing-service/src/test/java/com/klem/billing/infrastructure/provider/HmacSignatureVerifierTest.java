package com.klem.billing.infrastructure.provider;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HmacSignatureVerifierTest {

    @Test
    void validSignatureMatchesComputedDigest() {
        String payload = "{\"cpm_trans_id\":\"idem-1\",\"cpm_result\":\"00\"}";
        String secret = "test-secret";
        String signature = HmacSignatureVerifier.compute(payload, secret);

        assertThat(HmacSignatureVerifier.isValid(payload, signature, secret)).isTrue();
    }

    @Test
    void tamperedPayloadFailsVerification() {
        String secret = "test-secret";
        String signature = HmacSignatureVerifier.compute("{\"amount\":100}", secret);

        assertThat(HmacSignatureVerifier.isValid("{\"amount\":999}", signature, secret)).isFalse();
    }

    @Test
    void missingSignatureIsRejected() {
        assertThat(HmacSignatureVerifier.isValid("payload", null, "secret")).isFalse();
        assertThat(HmacSignatureVerifier.isValid("payload", "", "secret")).isFalse();
    }
}
