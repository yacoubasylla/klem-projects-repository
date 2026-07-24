package com.klem.cantine.parametrage.dto;

import com.klem.cantine.parametrage.entity.Configuration;
import java.time.LocalDateTime;

public record ConfigurationDTO(
        Long id,
        String cle,
        String valeur,
        String description,
        LocalDateTime dateModification
) {
    public static ConfigurationDTO from(Configuration c) {
        return new ConfigurationDTO(c.getId(), c.getCle(), c.getValeur(), c.getDescription(), c.getDateModification());
    }
}
