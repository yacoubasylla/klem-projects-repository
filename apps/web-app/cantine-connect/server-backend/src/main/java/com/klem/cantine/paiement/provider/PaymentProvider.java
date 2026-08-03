package com.klem.cantine.paiement.provider;

import com.klem.cantine.paiement.dto.InitierPaiementRequestDTO;

/**
 * Abstraction multi-rails pour l'initiation d'un paiement Mobile Money.
 * Chaque implémentation représente une passerelle (agrégateur CinetPay/PayDunya,
 * ou API marchande directe Orange/MTN/Moov). Le provider actif est sélectionné
 * via la configuration `PAIEMENT_PROVIDER_ACTIF` (voir PaiementService).
 */
public interface PaymentProvider {

    /** Identifiant stable utilisé par la config `PAIEMENT_PROVIDER_ACTIF` (ex. "CINETPAY"). */
    String getCode();

    /**
     * Construit l'URL de paiement à laquelle rediriger le payeur.
     * @param referenceInterne référence de la transaction déjà persistée en base
     */
    String initierPaiement(String referenceInterne, InitierPaiementRequestDTO dto);
}
