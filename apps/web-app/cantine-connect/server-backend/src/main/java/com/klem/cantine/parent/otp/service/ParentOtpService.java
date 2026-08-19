package com.klem.cantine.parent.otp.service;

import com.klem.cantine.auth.dto.AuthResponseDTO;
import com.klem.cantine.auth.entity.Role;
import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.auth.repository.UtilisateurRepository;
import com.klem.cantine.auth.service.JwtService;
import com.klem.cantine.notification.NotificationSender;
import com.klem.cantine.parametrage.service.ConfigurationService;
import com.klem.cantine.parent.entity.Parent;
import com.klem.cantine.parent.otp.OtpStore;
import com.klem.cantine.parent.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

/**
 * Accès parent par OTP (WhatsApp/SMS/Email) — remplace le formulaire "Demande d'accès" avec
 * validation admin par une vérification de numéro auto-suffisante : saisir le numéro + l'email,
 * puis le code reçu, crée le compte parent à la volée si ce numéro n'en a pas encore et délivre
 * directement un jeton de session. Décision explicite du 2026-08-18 (« pour des questions de
 * facilité ») qui remplace le contrôle admin préalable auparavant en place — voir
 * `collaboration/history/adr/` pour le détail et la décision antérieure qu'elle remplace.
 * <p>
 * Le mot de passe généré à la création n'est jamais communiqué : ce compte n'est destiné qu'à la
 * connexion par OTP (pas de flux "mot de passe oublié" pour lui).
 * <p>
 * Canal téléphone (WhatsApp/SMS) paramétrable par un ADMIN via la configuration {@code
 * PARENT_OTP_CANAL_TELEPHONE} (WhatsApp par défaut) — voir {@link #envoyerOtp}. Contrairement aux
 * autres notifications ({@code NotificationDispatcher}), l'envoi de l'OTP n'est pas soumis aux
 * bascules générales {@code NOTIFICATIONS_SMS_ENABLED}/{@code NOTIFICATIONS_WHATSAPP_ENABLED} :
 * c'est une étape fonctionnelle de connexion, pas une notification optionnelle.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ParentOtpService {

    /** Valeurs possibles de {@code PARENT_OTP_CANAL_TELEPHONE} ; WhatsApp par défaut si absente/invalide. */
    static final String CLE_CANAL_TELEPHONE = "PARENT_OTP_CANAL_TELEPHONE";
    private static final String CANAL_SMS = "SMS";
    private static final String CANAL_WHATSAPP = "WHATSAPP";
    private static final String CANAL_EMAIL = "EMAIL";

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHABET_MOT_DE_PASSE = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";

    private final UtilisateurRepository utilisateurRepository;
    private final ParentRepository parentRepository;
    private final OtpStore otpStore;
    private final List<NotificationSender> notificationSenders;
    private final ConfigurationService configurationService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Génère et envoie un code à 6 chiffres (valable 5 minutes) sur le canal téléphone configuré
     * (WhatsApp par défaut, ou SMS — voir {@code PARENT_OTP_CANAL_TELEPHONE}) et par email.
     * Fonctionne que ce numéro corresponde déjà à un compte parent ou non — l'email fourni est
     * conservé pour la création du compte à la vérification si besoin (voir {@link #verifierOtp}).
     */
    public void envoyerOtp(String whatsappNumber, String email) {
        String telephone = normaliser(whatsappNumber);
        String emailNormalise = email.trim().toLowerCase();
        Optional<Utilisateur> compteExistant =
                utilisateurRepository.findByTelephoneAndRoleAndActifTrue(telephone, Role.PARENT);

        String code = genererCode();
        otpStore.enregistrer(telephone, code, emailNormalise);

        String sujet = "Votre code de vérification Cantine Connect";
        String corps = "Votre code de vérification est : " + code
                + "\n\nValable 5 minutes. Ne le partagez avec personne.";
        String emailDestinataire = compteExistant.map(Utilisateur::getEmail).orElse(emailNormalise);

        String canalTelephone = resoudreCanalTelephone();
        envoyerSurCanal(canalTelephone, telephone, sujet, corps);
        envoyerSurCanal(CANAL_EMAIL, emailDestinataire, sujet, corps);

        log.info("Code OTP envoyé pour le numéro {} par {} (compte {})", telephone, canalTelephone,
                compteExistant.isPresent() ? "existant" : "à créer");
    }

    /** WhatsApp par défaut — bascule vers SMS uniquement si explicitement configuré ainsi. */
    private String resoudreCanalTelephone() {
        return CANAL_SMS.equalsIgnoreCase(configurationService.getValeur(CLE_CANAL_TELEPHONE))
                ? CANAL_SMS : CANAL_WHATSAPP;
    }

    private void envoyerSurCanal(String canal, String destinataire, String sujet, String corps) {
        notificationSenders.stream()
                .filter(s -> canal.equals(s.getCanal()))
                .findFirst()
                .ifPresent(s -> s.envoyer(destinataire, sujet, corps));
    }

    /**
     * Vérifie le code fourni. S'il est valide, retourne un jeton de session — le compte parent
     * est créé à la volée si ce numéro n'en avait pas encore (voir la JavaDoc de la classe).
     * @throws IllegalArgumentException si le code est incorrect, expiré ou déjà utilisé
     * @throws IllegalStateException si l'email associé est déjà utilisé par un autre compte
     */
    @Transactional
    public AuthResponseDTO verifierOtp(String whatsappNumber, String otpCode) {
        String telephone = normaliser(whatsappNumber);
        Optional<String> emailEnAttente = otpStore.verifierEtInvalider(telephone, otpCode);
        if (emailEnAttente.isEmpty()) {
            throw new IllegalArgumentException("Code de vérification incorrect ou expiré");
        }

        Utilisateur utilisateur = utilisateurRepository
                .findByTelephoneAndRoleAndActifTrue(telephone, Role.PARENT)
                .orElseGet(() -> creerCompteParent(telephone, emailEnAttente.get()));

        String token = jwtService.generateToken(utilisateur);
        return AuthResponseDTO.of(token, jwtService.getExpirationMs(), utilisateur);
    }

    /**
     * Crée le compte PARENT et son profil {@link Parent} associé pour un numéro vérifié qui n'en
     * avait pas encore — aucun nom/prénom n'est collecté à cette étape (non demandé par ce
     * formulaire), remplacés par un intitulé générique modifiable ensuite par l'ADMIN si besoin.
     */
    private Utilisateur creerCompteParent(String telephone, String email) {
        if (utilisateurRepository.existsByEmail(email)) {
            throw new IllegalStateException("Un compte existe déjà avec cet email : " + email);
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .nom("Parent")
                .prenom(telephone)
                .email(email)
                .telephone(telephone)
                .motDePasse(passwordEncoder.encode(genererMotDePasseAleatoire()))
                .role(Role.PARENT)
                .build();
        utilisateur = utilisateurRepository.save(utilisateur);

        parentRepository.save(Parent.builder().utilisateur(utilisateur).build());

        log.info("Compte parent créé automatiquement par OTP pour le numéro {}", telephone);
        return utilisateur;
    }

    private String genererCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    private String genererMotDePasseAleatoire() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 24; i++) {
            sb.append(ALPHABET_MOT_DE_PASSE.charAt(RANDOM.nextInt(ALPHABET_MOT_DE_PASSE.length())));
        }
        return sb.toString();
    }

    /** Numéros locaux ("07XXXXXXXX") convertis en E.164 Côte d'Ivoire (+225...). */
    private String normaliser(String numero) {
        String nettoye = numero.replaceAll("[\\s-]", "");
        if (nettoye.startsWith("+")) return nettoye;
        if (nettoye.startsWith("0")) return "+225" + nettoye.substring(1);
        return "+225" + nettoye;
    }
}
