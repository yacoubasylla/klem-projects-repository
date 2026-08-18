package com.klem.cantine.parent.otp.service;

import com.klem.cantine.auth.dto.AuthResponseDTO;
import com.klem.cantine.auth.entity.Role;
import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.auth.repository.UtilisateurRepository;
import com.klem.cantine.auth.service.JwtService;
import com.klem.cantine.notification.NotificationDispatcher;
import com.klem.cantine.parent.otp.OtpStore;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

/**
 * Connexion parent sans mot de passe, par code de vérification (OTP) envoyé sur le numéro
 * WhatsApp/téléphone déjà rattaché à un compte {@link Role#PARENT} actif — <b>jamais</b> de
 * création de compte à la volée : un numéro sans compte est explicitement redirigé vers le
 * formulaire de demande d'accès existant (`DemandeAccesService`), pour préserver le contrôle
 * admin déjà en place (voir `decision-log.md`, refus de l'activation immédiate sans validation).
 * <p>
 * Une fois vérifié, l'OTP délivre exactement le même jeton qu'une connexion par mot de passe
 * ({@code AuthService#login}) — le parent accède ensuite aux mêmes endpoints existants
 * (`/parents/moi`, `/parents/moi/enfants`...), sans duplication de logique métier.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ParentOtpService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UtilisateurRepository utilisateurRepository;
    private final OtpStore otpStore;
    private final NotificationDispatcher notificationDispatcher;
    private final JwtService jwtService;

    /**
     * Génère et envoie un code à 6 chiffres (valable 5 minutes) sur les canaux actifs
     * (WhatsApp/SMS/Email) du compte parent correspondant à ce numéro.
     * @throws EntityNotFoundException si aucun compte PARENT actif n'est rattaché à ce numéro
     */
    public void envoyerOtp(String whatsappNumber) {
        String telephone = normaliser(whatsappNumber);
        Utilisateur utilisateur = trouverParentActif(telephone);

        String code = genererCode();
        otpStore.enregistrer(telephone, code);

        String sujet = "Votre code de vérification Cantine Connect";
        String corps = "Votre code de vérification est : " + code
                + "\n\nValable 5 minutes. Ne le partagez avec personne.";
        notificationDispatcher.envoyer(utilisateur.getEmail(), utilisateur.getTelephone(), sujet, corps);

        log.info("Code OTP envoyé pour le numéro {}", telephone);
    }

    /**
     * Vérifie le code fourni et, si valide, retourne un jeton de session identique à celui
     * d'une connexion par mot de passe.
     * @throws IllegalArgumentException si le code est incorrect, expiré ou déjà utilisé
     * @throws EntityNotFoundException si le compte n'existe plus entre l'envoi et la vérification
     */
    public AuthResponseDTO verifierOtp(String whatsappNumber, String otpCode) {
        String telephone = normaliser(whatsappNumber);
        if (!otpStore.verifierEtInvalider(telephone, otpCode)) {
            throw new IllegalArgumentException("Code de vérification incorrect ou expiré");
        }

        Utilisateur utilisateur = trouverParentActif(telephone);
        String token = jwtService.generateToken(utilisateur);
        return AuthResponseDTO.of(token, jwtService.getExpirationMs(), utilisateur);
    }

    private Utilisateur trouverParentActif(String telephone) {
        return utilisateurRepository.findByTelephoneAndRoleAndActifTrue(telephone, Role.PARENT)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucun compte parent actif n'est rattaché à ce numéro. "
                                + "Soumettez une demande d'accès pour en créer un."));
    }

    private String genererCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    /** Numéros locaux ("07XXXXXXXX") convertis en E.164 Côte d'Ivoire (+225...). */
    private String normaliser(String numero) {
        String nettoye = numero.replaceAll("[\\s-]", "");
        if (nettoye.startsWith("+")) return nettoye;
        if (nettoye.startsWith("0")) return "+225" + nettoye.substring(1);
        return "+225" + nettoye;
    }
}
