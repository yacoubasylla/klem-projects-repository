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
    private OrangeMoneyProps orangeMoney = new OrangeMoneyProps();

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

    /**
     * Intégration marchande directe Orange Money Webpayment CI (OAuth2 client_credentials).
     * Remplace, une fois les identifiants marchands obtenus, le placeholder historique
     * {@code OrangeMoneyDirectProvider}.
     */
    @Getter
    @Setter
    public static class OrangeMoneyProps {
        private String clientId;
        private String clientSecret;
        private String merchantKey;
        private String authUrl = "https://api.orange.com/oauth/v3/token";
        private String webpaymentUrl = "https://api.orange.com/orange-money-webpay/ci/v1/webpayment";
        private String webhookSecret;
        private boolean verifySignature = false;
    }
}
