package com.klem.cantine.etablissement.dto;

import com.klem.cantine.etablissement.entity.Niveau;
import java.util.List;

public record NiveauResponseDTO(
    Long id,
    String libelle,
    Integer ordre,
    List<ClasseResponseDTO> classes
) {
    public static NiveauResponseDTO from(Niveau n) {
        return new NiveauResponseDTO(
            n.getId(),
            n.getLibelle(),
            n.getOrdre(),
            n.getClasses().stream().map(ClasseResponseDTO::from).toList()
        );
    }
}
