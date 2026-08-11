package com.klem.referentielapi.shared.domain;

/**
 * Exception métier de base pour « ressource introuvable » — chaque domaine dérive sa propre
 * exception spécifique (ex. {@code TexteReglementaireNotFoundException}) plutôt que de lever
 * celle-ci directement, pour garder un message et un code d'erreur explicites par ressource.
 */
public abstract class NotFoundException extends RuntimeException {

    protected NotFoundException(String message) {
        super(message);
    }
}
