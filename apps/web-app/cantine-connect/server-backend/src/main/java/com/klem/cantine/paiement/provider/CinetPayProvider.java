package com.klem.cantine.paiement.provider;

import com.klem.cantine.paiement.config.PaiementProperties;
import com.klem.cantine.paiement.dto.InitierPaiementRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CinetPayProvider implements PaymentProvider {

    private final PaiementProperties paiementProperties;

    @Override
    public String getCode() {
        return "CINETPAY";
    }

    @Override
    public String initierPaiement(String referenceInterne, InitierPaiementRequestDTO dto) {
        // En production : appel à l'API CinetPay (POST /v2/payment) pour obtenir
        // l'URL réelle du checkout — cette construction directe est un simplifié
        // suffisant pour le sandbox/dev.
        String siteId = paiementProperties.getCinetpay().getSiteId();
        return String.format(
                "https://checkout.cinetpay.com/pay?site_id=%s&transaction_id=%s&amount=%s&currency=XOF&phone=%s",
                siteId, referenceInterne, dto.montant(), dto.telephonePayeur()
        );
    }
}
