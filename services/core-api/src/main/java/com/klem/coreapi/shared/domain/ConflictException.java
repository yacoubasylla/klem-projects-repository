package com.klem.coreapi.shared.domain;

/** Exception métier de base pour « état déjà existant, action incompatible avec l'état courant ». */
public abstract class ConflictException extends RuntimeException {

    protected ConflictException(String message) {
        super(message);
    }
}
