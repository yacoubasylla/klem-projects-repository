package com.klem.cantine.scan.dto;

import com.klem.cantine.scan.entity.MotifRefus;
import com.klem.cantine.scan.entity.ResultatScan;

public record ModifierPassageRequestDTO(
        ResultatScan resultat,
        MotifRefus motifRefus
) {}
