package com.klem.cantine.parametrage.service;

import com.klem.cantine.actionlog.annotation.Traceable;
import com.klem.cantine.actionlog.entity.TypeAction;
import com.klem.cantine.auth.entity.Role;
import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.auth.repository.UtilisateurRepository;
import com.klem.cantine.notification.NotificationService;
import com.klem.cantine.parametrage.dto.DemandeAccesRequestDTO;
import com.klem.cantine.parametrage.dto.DemandeAccesResponseDTO;
import com.klem.cantine.parametrage.dto.ValiderDemandeResponseDTO;
import com.klem.cantine.parametrage.entity.DemandeAcces;
import com.klem.cantine.parametrage.entity.StatutDemande;
import com.klem.cantine.parametrage.repository.DemandeAccesRepository;
import com.klem.cantine.parent.entity.Parent;
import com.klem.cantine.parent.repository.ParentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DemandeAccesService {

    // Domaine synthétique utilisé comme identifiant de connexion pour les parents
    // n'ayant fourni aucun email (le téléphone reste le canal de notification réel).
    public static final String DOMAINE_EMAIL_SYNTHETIQUE = "parent.cantine-connect.ci";

    private static final String ALPHABET_MOT_DE_PASSE = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final DemandeAccesRepository demandeAccesRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ParentRepository parentRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @Traceable(action = TypeAction.CREATE, entite = "DemandeAcces")
    @Transactional
    public DemandeAccesResponseDTO soumettre(DemandeAccesRequestDTO dto) {
        String telephonePrincipal = normaliserTelephone(dto.telephonePrincipal());
        DemandeAcces demande = DemandeAcces.builder()
                .nom(dto.nom())
                .prenom(dto.prenom())
                .fonction(dto.fonction())
                .telephonePrincipal(telephonePrincipal)
                // Téléphone WhatsApp vide ⇒ identique au numéro principal
                .telephoneWhatsapp(
                        dto.telephoneWhatsapp() != null && !dto.telephoneWhatsapp().isBlank()
                                ? normaliserTelephone(dto.telephoneWhatsapp()) : telephonePrincipal)
                .telephoneSecondaire(
                        dto.telephoneSecondaire() != null && !dto.telephoneSecondaire().isBlank()
                                ? normaliserTelephone(dto.telephoneSecondaire()) : null)
                .email(dto.email() != null && !dto.email().isBlank() ? dto.email().trim().toLowerCase() : null)
                .ville(dto.ville())
                .commune(dto.commune())
                .quartier(dto.quartier())
                .build();
        return DemandeAccesResponseDTO.from(demandeAccesRepository.save(demande));
    }

    public Page<DemandeAccesResponseDTO> lister(StatutDemande statut, Pageable pageable) {
        Page<DemandeAcces> page = statut != null
                ? demandeAccesRepository.findByStatut(statut, pageable)
                : demandeAccesRepository.findAll(pageable);
        return page.map(DemandeAccesResponseDTO::from);
    }

    @Traceable(action = TypeAction.UPDATE, entite = "DemandeAcces")
    @Transactional
    public ValiderDemandeResponseDTO valider(Long id, Utilisateur admin) {
        DemandeAcces demande = trouver(id);
        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new IllegalStateException("Cette demande a déjà été traitée.");
        }

        if (utilisateurRepository.existsByTelephone(demande.getTelephonePrincipal())) {
            throw new IllegalStateException(
                    "Un compte existe déjà avec ce numéro de téléphone : " + demande.getTelephonePrincipal());
        }

        boolean emailReel = demande.getEmail() != null && !demande.getEmail().isBlank();
        String email = emailReel ? demande.getEmail() : genererEmailSynthetique(demande.getTelephonePrincipal());
        if (utilisateurRepository.existsByEmail(email)) {
            throw new IllegalStateException("Un compte existe déjà avec cet email : " + email);
        }

        String motDePasseTemporaire = genererMotDePasseTemporaire();

        Utilisateur utilisateur = Utilisateur.builder()
                .nom(demande.getNom())
                .prenom(demande.getPrenom())
                .email(email)
                .telephone(demande.getTelephonePrincipal())
                .motDePasse(passwordEncoder.encode(motDePasseTemporaire))
                .role(Role.PARENT)
                .doitChangerMotDePasse(true)
                .build();
        utilisateur = utilisateurRepository.save(utilisateur);

        Parent parent = Parent.builder().utilisateur(utilisateur).build();
        parentRepository.save(parent);

        demande.setStatut(StatutDemande.VALIDEE);
        demande.setDateTraitement(java.time.LocalDateTime.now());
        demande.setTraitePar(admin.getEmail());
        demandeAccesRepository.save(demande);

        // Email uniquement si réel (pas le domaine synthétique) — le téléphone reste
        // toujours un canal valide.
        notificationService.notifierDemandeAccesValidee(
                emailReel ? email : null, demande.getTelephonePrincipal(), email, motDePasseTemporaire);

        return new ValiderDemandeResponseDTO(DemandeAccesResponseDTO.from(demande), email, motDePasseTemporaire);
    }

    @Traceable(action = TypeAction.UPDATE, entite = "DemandeAcces")
    @Transactional
    public DemandeAccesResponseDTO rejeter(Long id, String motif, Utilisateur admin) {
        DemandeAcces demande = trouver(id);
        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new IllegalStateException("Cette demande a déjà été traitée.");
        }
        demande.setStatut(StatutDemande.REJETEE);
        demande.setMotifRejet(motif);
        demande.setDateTraitement(java.time.LocalDateTime.now());
        demande.setTraitePar(admin.getEmail());
        return DemandeAccesResponseDTO.from(demandeAccesRepository.save(demande));
    }

    private DemandeAcces trouver(Long id) {
        return demandeAccesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Demande d'accès introuvable : " + id));
    }

    // Retire les séparateurs de saisie (espace, point, tiret) pour que deux numéros identiques
    // saisis sous des formats différents ("07 08 09 10 11" vs "0708091011") soient stockés à
    // l'identique — condition nécessaire pour que la contrainte unique sur Utilisateur.telephone
    // et le contrôle existsByTelephone() détectent réellement les doublons.
    private String normaliserTelephone(String telephone) {
        return telephone.replaceAll("[\\s.-]", "");
    }

    private String genererEmailSynthetique(String telephonePrincipal) {
        String chiffres = telephonePrincipal.replaceAll("\\D", "");
        return "p" + chiffres + "@" + DOMAINE_EMAIL_SYNTHETIQUE;
    }

    private String genererMotDePasseTemporaire() {
        StringBuilder sb = new StringBuilder("CC-");
        for (int i = 0; i < 8; i++) {
            sb.append(ALPHABET_MOT_DE_PASSE.charAt(RANDOM.nextInt(ALPHABET_MOT_DE_PASSE.length())));
        }
        return sb.toString();
    }
}
