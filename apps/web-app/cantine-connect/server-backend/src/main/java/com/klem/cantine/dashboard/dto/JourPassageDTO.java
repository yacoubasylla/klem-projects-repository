package com.klem.cantine.dashboard.dto;

import java.time.LocalDate;

public record JourPassageDTO(LocalDate date, long accordes, long refuses) {
    public long total() { return accordes + refuses; }
}
