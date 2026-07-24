package com.klem.cantine.paiement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WebhookCinetPayDTO(

        @JsonProperty("cpm_site_id")
        String cpmSiteId,

        // Contient notre referenceInterne
        @JsonProperty("cpm_trans_id")
        String cpmTransId,

        @JsonProperty("cpm_amount")
        String cpmAmount,

        @JsonProperty("cpm_currency")
        String cpmCurrency,

        // "00" = succès, toute autre valeur = échec
        @JsonProperty("cpm_result")
        String cpmResult,

        // "ACCEPTED" ou "REFUSED"
        @JsonProperty("cpm_trans_status")
        String cpmTransStatus,

        @JsonProperty("cpm_payid")
        String cpmPayid,

        @JsonProperty("cel_phone_num")
        String celPhoneNum,

        String signature
) {
    public boolean estAccepte() {
        return "00".equals(cpmResult) || "ACCEPTED".equalsIgnoreCase(cpmTransStatus);
    }
}
