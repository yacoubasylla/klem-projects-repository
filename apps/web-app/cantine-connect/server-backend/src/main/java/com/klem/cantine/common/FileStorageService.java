package com.klem.cantine.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Stockage disque local pour les fichiers uploadés (ex. certificats médicaux).
 * ⚠️ Limite connue : sur un hébergement à disque éphémère (ex. Railway), les
 * fichiers ne survivent pas à un redéploiement. Prévoir en phase 2 une bascule
 * vers un stockage objet (S3-compatible) — non bloquant pour le MVP local/Docker.
 */
@Service
@Slf4j
public class FileStorageService {

    @Value("${storage.uploads-dir:uploads}")
    private String uploadsDir;

    public String enregistrerCertificatMedical(Long eleveId, MultipartFile fichier) {
        String extension = extensionDe(fichier.getOriginalFilename());
        String nomFichier = UUID.randomUUID() + extension;
        Path dossier = Path.of(uploadsDir, "certificats");
        Path cible = dossier.resolve(nomFichier);
        try {
            Files.createDirectories(dossier);
            fichier.transferTo(cible);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Échec de l'enregistrement du certificat médical pour l'élève " + eleveId, e);
        }
        String url = "/uploads/certificats/" + nomFichier;
        log.info("Certificat médical enregistré pour l'élève {} : {}", eleveId, url);
        return url;
    }

    public String enregistrerLogo(MultipartFile fichier) {
        String extension = extensionDe(fichier.getOriginalFilename());
        String nomFichier = "logo-" + UUID.randomUUID() + extension;
        Path dossier = Path.of(uploadsDir, "branding");
        Path cible = dossier.resolve(nomFichier);
        try {
            Files.createDirectories(dossier);
            fichier.transferTo(cible);
        } catch (IOException e) {
            throw new UncheckedIOException("Échec de l'enregistrement du logo", e);
        }
        String url = "/uploads/branding/" + nomFichier;
        log.info("Logo organisation enregistré : {}", url);
        return url;
    }

    private String extensionDe(String nomOriginal) {
        if (nomOriginal == null) return "";
        int i = nomOriginal.lastIndexOf('.');
        return i >= 0 ? nomOriginal.substring(i) : "";
    }
}
