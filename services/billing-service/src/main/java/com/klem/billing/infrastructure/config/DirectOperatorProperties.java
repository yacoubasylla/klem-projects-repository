package com.klem.billing.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Secrets applicatifs par opérateur en intégration API directe — `billing.direct-operators.*`. */
@ConfigurationProperties(prefix = "billing.direct-operators")
public class DirectOperatorProperties {

    private Provider wave = new Provider();
    private Provider orangeMoney = new Provider();
    private Provider mtnMobileMoney = new Provider();
    private Provider moovMoney = new Provider();

    public Provider getWave() { return wave; }
    public void setWave(Provider wave) { this.wave = wave; }
    public Provider getOrangeMoney() { return orangeMoney; }
    public void setOrangeMoney(Provider orangeMoney) { this.orangeMoney = orangeMoney; }
    public Provider getMtnMobileMoney() { return mtnMobileMoney; }
    public void setMtnMobileMoney(Provider mtnMobileMoney) { this.mtnMobileMoney = mtnMobileMoney; }
    public Provider getMoovMoney() { return moovMoney; }
    public void setMoovMoney(Provider moovMoney) { this.moovMoney = moovMoney; }

    public static class Provider {
        private String baseUrl;
        private String clientId;
        private String clientSecret;
        private String merchantCode;

        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }
        public String getClientSecret() { return clientSecret; }
        public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
        public String getMerchantCode() { return merchantCode; }
        public void setMerchantCode(String merchantCode) { this.merchantCode = merchantCode; }
    }
}
