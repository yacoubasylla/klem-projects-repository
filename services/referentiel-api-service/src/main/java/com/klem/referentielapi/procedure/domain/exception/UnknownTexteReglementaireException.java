package com.klem.referentielapi.procedure.domain.exception;

import com.klem.referentielapi.shared.domain.BadRequestException;

import java.util.UUID;

/** Levée quand une association vers un {@code texteId} qui n'existe pas est demandée. */
public class UnknownTexteReglementaireException extends BadRequestException {

    public UnknownTexteReglementaireException(UUID texteId) {
        super("Texte réglementaire inconnu : " + texteId);
    }
}
