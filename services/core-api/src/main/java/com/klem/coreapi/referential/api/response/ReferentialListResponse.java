package com.klem.coreapi.referential.api.response;

import com.klem.coreapi.referential.domain.model.ReferentialEntry;
import com.klem.coreapi.referential.domain.model.ReferentialList;

import java.util.List;

public record ReferentialListResponse(String code, String label, List<ReferentialEntryResponse> entries) {

    public static ReferentialListResponse from(ReferentialList list, List<ReferentialEntry> entries) {
        return new ReferentialListResponse(
                list.getCode(),
                list.getLabel(),
                entries.stream().map(ReferentialEntryResponse::from).toList()
        );
    }
}
