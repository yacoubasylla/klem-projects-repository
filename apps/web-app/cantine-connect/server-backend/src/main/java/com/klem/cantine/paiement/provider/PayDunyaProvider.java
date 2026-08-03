package com.klem.cantine.paiement.provider;

import com.klem.cantine.paiement.config.PaiementProperties;
import com.klem.cantine.paiement.dto.InitierPaiementRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayDunyaProvider implements PaymentProvider {

    private final PaiementProperties paiementProperties;

    @Override
    public String getCode() {
        return "PAYDUNYA";
    }

    @Override
    public String initierPaiement(String referenceInterne, InitierPaiementRequestDTO dto) {
        // En production : appel à l'API PayDunya (POST /v1/checkout-invoice/create)
        // pour obtenir le token et l'URL réelle du checkout — construction directe
        // suffisante pour le sandbox/dev, symétrique à CinetPayProvider.
        String token = paiementProperties.getPaydunya().getToken();
        return String.format(
                "https://paydunya.com/checkout/invoice/%s?ref=%s&amount=%s&currency=XOF&phone=%s",
                token, referenceInterne, dto.montant(), dto.telephonePayeur()
        );
    }
}
