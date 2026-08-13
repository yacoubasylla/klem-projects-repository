package com.klem.billing.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Secrets applicatifs par agrégateur — jamais en dur, injectés via variables d'environnement
 * (spécifications_techniques.md §3). Un bloc par agrégateur sous `billing.aggregators.*` dans
 * application.yml.
 */
@ConfigurationProperties(prefix = "billing.aggregators")
public class AggregatorProperties {

    private Provider cinetpay = new Provider();
    private Provider bizao = new Provider();
    private Provider fedapay = new Provider();
    private Provider paydunya = new Provider();

    public Provider getCinetpay() { return cinetpay; }
    public void setCinetpay(Provider cinetpay) { this.cinetpay = cinetpay; }
    public Provider getBizao() { return bizao; }
    public void setBizao(Provider bizao) { this.bizao = bizao; }
    public Provider getFedapay() { return fedapay; }
    public void setFedapay(Provider fedapay) { this.fedapay = fedapay; }
    public Provider getPaydunya() { return paydunya; }
    public void setPaydunya(Provider paydunya) { this.paydunya = paydunya; }

    public static class Provider {
        private String baseUrl;
        private String apiKey;
        private String apiSecret;
        private String siteId;

        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getApiSecret() { return apiSecret; }
        public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }
        public String getSiteId() { return siteId; }
        public void setSiteId(String siteId) { this.siteId = siteId; }
    }
}
