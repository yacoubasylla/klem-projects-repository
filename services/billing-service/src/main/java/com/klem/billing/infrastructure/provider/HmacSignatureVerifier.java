package com.klem.billing.infrastructure.provider;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * HMAC-SHA256 partagé par les providers qui signent leurs callbacks de cette façon (CinetPay,
 * PayDunya, Bizao — même principe que la vérification déjà en place côté `cantine_connect`).
 * Fedapay et les opérateurs directs ont chacun leur propre schéma, implémenté dans leur provider.
 */
public final class HmacSignatureVerifier {

    private static final String ALGORITHM = "HmacSHA256";

    private HmacSignatureVerifier() {
    }

    public static boolean isValid(String payload, String receivedSignatureHex, String secret) {
        if (receivedSignatureHex == null || receivedSignatureHex.isBlank()) {
            return false;
        }
        String computed = compute(payload, secret);
        return constantTimeEquals(computed, receivedSignatureHex.toLowerCase());
    }

    public static String compute(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM));
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("HMAC-SHA256 indisponible", e);
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
