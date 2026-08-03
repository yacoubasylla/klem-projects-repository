package com.klem.cantine.paiement.provider;

import com.klem.cantine.paiement.dto.InitierPaiementRequestDTO;
import org.springframework.stereotype.Component;

/**
 * Placeholder — intégration marchande directe MTN Mobile Money Côte d'Ivoire
 * (bypass des agrégateurs CinetPay/PayDunya). Nécessite un accès marchand MTN
 * (identifiants API MoMo) non disponible à ce stade.
 */
@Component
public class MtnMoneyDirectProvider implements PaymentProvider {

    @Override
    public String getCode() {
        return "MTN_MONEY_DIRECT";
    }

    @Override
    public String initierPaiement(String referenceInterne, InitierPaiementRequestDTO dto) {
        throw new UnsupportedOperationException(
                "Intégration directe MTN Money non disponible — accès marchand requis. " +
                "Utiliser le provider CINETPAY ou PAYDUNYA en attendant.");
    }
}
