package com.klem.cantine.parametrage.service;

import com.klem.cantine.common.FileStorageService;
import com.klem.cantine.parametrage.dto.ConfigurationDTO;
import com.klem.cantine.parametrage.entity.Configuration;
import com.klem.cantine.parametrage.repository.ConfigurationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;
    private final FileStorageService fileStorageService;

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

    @Transactional
    public ConfigurationDTO uploaderLogo(MultipartFile fichier) {
        if (fichier == null || fichier.isEmpty()) {
            throw new IllegalArgumentException("Le fichier du logo est requis.");
        }
        String contentType = fichier.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Le logo doit être une image (PNG, JPEG, WebP, SVG...).");
        }
        String url = fileStorageService.enregistrerLogo(fichier);
        return modifier("ORGANISATION_LOGO_URL", url);
    }

    @Transactional
    public ConfigurationDTO uploaderFondEcran(MultipartFile fichier) {
        if (fichier == null || fichier.isEmpty()) {
            throw new IllegalArgumentException("Le fichier de l'image de fond est requis.");
        }
        String contentType = fichier.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("L'image de fond doit être une image (PNG, JPEG, WebP...).");
        }
        String url = fileStorageService.enregistrerFondEcran(fichier);
        return modifier("FOND_ECRAN_LOGIN", url);
    }
}
