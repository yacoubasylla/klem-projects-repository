package com.klem.cantine.parametrage.service;

import com.klem.cantine.parametrage.dto.ConfigurationDTO;
import com.klem.cantine.parametrage.entity.Configuration;
import com.klem.cantine.parametrage.repository.ConfigurationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    public List<ConfigurationDTO> listerToutes() {
        return configurationRepository.findAll().stream()
                .map(ConfigurationDTO::from)
                .toList();
    }

    public ConfigurationDTO getParCle(String cle) {
        return configurationRepository.findByCle(cle)
                .map(ConfigurationDTO::from)
                .orElseThrow(() -> new EntityNotFoundException("Configuration introuvable : " + cle));
    }

    public String getValeur(String cle) {
        return configurationRepository.findByCle(cle)
                .map(Configuration::getValeur)
                .orElse("false");
    }

    @Transactional
    public ConfigurationDTO modifier(String cle, String valeur) {
        Configuration config = configurationRepository.findByCle(cle)
                .orElseThrow(() -> new EntityNotFoundException("Configuration introuvable : " + cle));
        config.setValeur(valeur);
        return ConfigurationDTO.from(configurationRepository.save(config));
    }
}
