package com.klem.referentielapi.shared.domain;

/** Exception métier de base pour « valeur syntaxiquement acceptée mais sémantiquement invalide ». */
public abstract class BadRequestException extends RuntimeException {

    protected BadRequestException(String message) {
        super(message);
    }
}
