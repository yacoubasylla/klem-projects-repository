package com.klem.cantine.notification;

import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.parent.entity.Parent;
import com.klem.cantine.parent.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Notifications parent sur les événements clés (paiement, passage cantine).
 * Délègue l'envoi effectif à {@link NotificationDispatcher}, qui répartit sur
 * les canaux actifs (email aujourd'hui, SMS en stub — voir NotificationSender).
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationDispatcher notificationDispatcher;
    private final ParentRepository parentRepository;

    @Async
    public void notifierPaiementAccepte(Eleve eleve, BigDecimal montant) {
        String sujet = "✅ Paiement confirmé — " + eleve.getPrenom() + " " + eleve.getNom();
        String corps = String.format(
            "Bonjour,\n\n" +
            "Le paiement de %.0f FCFA pour %s %s a été accepté et le compte d'accès à la cantine est activé.\n\n" +
            "Date : %s\n\n" +
            "Cordialement,\nL'équipe Cantine Connect",
            montant, eleve.getPrenom(), eleve.getNom(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        notificationDispatcher.envoyer(findParentEmail(eleve).orElse(null), findParentTelephone(eleve).orElse(null), sujet, corps);
    }

    @Async
    public void notifierDemandeAccesValidee(String email, String telephone, String identifiant, String motDePasseTemporaire) {
        String sujet = "🎉 Votre accès Cantine Connect est activé";
        String corps = String.format(
            "Bonjour,\n\n" +
            "Votre demande d'accès a été validée. Voici vos identifiants de connexion :\n\n" +
            "Identifiant : %s\n" +
            "Mot de passe temporaire : %s\n\n" +
            "Vous devrez le modifier dès votre première connexion.\n\n" +
            "Cordialement,\nL'équipe Cantine Connect",
            identifiant, motDePasseTemporaire
        );
        notificationDispatcher.envoyer(email, telephone, sujet, corps);
    }

    @Async
    public void notifierPassageCantine(Eleve eleve) {
        String sujet = "🍽️ Passage cantine — " + eleve.getPrenom() + " " + eleve.getNom();
        String corps = String.format(
            "Bonjour,\n\n" +
            "%s %s vient de prendre son repas à la cantine.\n\n" +
            "Heure : %s\n\n" +
            "Cordialement,\nL'équipe Cantine Connect",
            eleve.getPrenom(), eleve.getNom(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        notificationDispatcher.envoyer(findParentEmail(eleve).orElse(null), findParentTelephone(eleve).orElse(null), sujet, corps);
    }

    private Optional<String> findParentEmail(Eleve eleve) {
        // Priorité au compte parent lié, sinon l'email renseigné sur la fiche élève
        return parentLie(eleve)
                .map(Parent::getUtilisateur)
                .map(u -> u.getEmail())
                .or(() -> Optional.ofNullable(eleve.getParentEmail()));
    }

    private Optional<String> findParentTelephone(Eleve eleve) {
        // Priorité au compte parent lié, sinon le téléphone renseigné sur la fiche élève
        return parentLie(eleve)
                .map(Parent::getUtilisateur)
                .map(u -> u.getTelephone())
                .or(() -> Optional.ofNullable(eleve.getParentTelephone()));
    }

    private Optional<Parent> parentLie(Eleve eleve) {
        return parentRepository.findAll().stream()
                .filter(p -> p.getEnfants().stream().anyMatch(e -> e.getId().equals(eleve.getId())))
                .findFirst();
    }
}
