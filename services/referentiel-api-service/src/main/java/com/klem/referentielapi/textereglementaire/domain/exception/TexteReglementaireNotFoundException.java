package com.klem.referentielapi.textereglementaire.domain.exception;

import com.klem.referentielapi.shared.domain.NotFoundException;

import java.util.UUID;

public class TexteReglementaireNotFoundException extends NotFoundException {

    public TexteReglementaireNotFoundException(UUID id) {
        super("Texte réglementaire introuvable : " + id);
    }
}
