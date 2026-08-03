package com.klem.cantine.paiement.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "paiement")
@Getter
@Setter
public class PaiementProperties {

    private CinetPayProps cinetpay = new CinetPayProps();
    private PayDunyaProps paydunya = new PayDunyaProps();

    @Getter
    @Setter
    public static class CinetPayProps {
        private String apiKey;
        private String siteId;
        private String apiSecret;
        private boolean verifySignature = false;
    }

    @Getter
    @Setter
    public static class PayDunyaProps {
        private String masterKey;
        private String privateKey;
        private String publicKey;
        private String token;
        // "test" (sandbox-api) ou "live" (api) — détermine l'hôte PayDunya appelé.
        private String mode = "test";
        private boolean verifySignature = false;
    }
}
