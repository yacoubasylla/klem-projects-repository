package com.klem.cantine.paiement.provider;

import com.klem.cantine.paiement.dto.InitierPaiementRequestDTO;
import org.springframework.stereotype.Component;

/**
 * Placeholder — intégration marchande directe Orange Money Côte d'Ivoire
 * (bypass des agrégateurs CinetPay/PayDunya). Nécessite un accès marchand
 * Orange (identifiants API) non disponible à ce stade.
 */
@Component
public class OrangeMoneyDirectProvider implements PaymentProvider {

    @Override
    public String getCode() {
        return "ORANGE_MONEY_DIRECT";
    }

    @Override
    public String initierPaiement(String referenceInterne, InitierPaiementRequestDTO dto) {
        throw new UnsupportedOperationException(
                "Intégration directe Orange Money non disponible — accès marchand requis. " +
                "Utiliser le provider CINETPAY ou PAYDUNYA en attendant.");
    }
}
