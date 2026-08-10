package com.klem.coreapi.referential.api.response;

import com.klem.coreapi.referential.domain.model.ReferentialEntry;

public record ReferentialEntryResponse(String code, String label) {

    public static ReferentialEntryResponse from(ReferentialEntry entry) {
        return new ReferentialEntryResponse(entry.getEntryCode(), entry.getLabel());
    }
}
