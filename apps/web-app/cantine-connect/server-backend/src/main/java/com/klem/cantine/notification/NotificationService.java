package com.klem.cantine.notification;

import com.klem.cantine.eleve.entity.Eleve;
import com.klem.cantine.parametrage.service.ConfigurationService;
import com.klem.cantine.parent.entity.Parent;
import com.klem.cantine.parent.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ObjectProvider<MailSender> mailSenderProvider;
    private final ConfigurationService configurationService;
    private final ParentRepository parentRepository;

    @Value("${notification.from:noreply@cantine-connect.ci}")
    private String fromAddress;

    @Async
    public void notifierPaiementAccepte(Eleve eleve, BigDecimal montant) {
        if (!emailActive()) return;
        findParentEmail(eleve).ifPresent(email -> {
            String sujet = "✅ Paiement confirmé — " + eleve.getPrenom() + " " + eleve.getNom();
            String corps = String.format(
                "Bonjour,\n\n" +
                "Le paiement de %.0f FCFA pour %s %s a été accepté et le compte d'accès à la cantine est activé.\n\n" +
                "Date : %s\n\n" +
                "Cordialement,\nL'équipe Cantine Connect",
                montant, eleve.getPrenom(), eleve.getNom(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
            envoyerEmail(email, sujet, corps);
        });
    }

    @Async
    public void notifierPassageCantine(Eleve eleve) {
        if (!emailActive()) return;
        findParentEmail(eleve).ifPresent(email -> {
            String sujet = "🍽️ Passage cantine — " + eleve.getPrenom() + " " + eleve.getNom();
            String corps = String.format(
                "Bonjour,\n\n" +
                "%s %s vient de prendre son repas à la cantine.\n\n" +
                "Heure : %s\n\n" +
                "Cordialement,\nL'équipe Cantine Connect",
                eleve.getPrenom(), eleve.getNom(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
            envoyerEmail(email, sujet, corps);
        });
    }

    private void envoyerEmail(String destinataire, String sujet, String corps) {
        MailSender sender = mailSenderProvider.getIfAvailable();
        if (sender == null) {
            log.debug("MailSender non configuré — email non envoyé à {}", destinataire);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(destinataire);
            message.setSubject(sujet);
            message.setText(corps);
            sender.send(message);
            log.info("Email envoyé à {} — sujet : {}", destinataire, sujet);
        } catch (Exception e) {
            log.warn("Échec envoi email à {} : {}", destinataire, e.getMessage());
        }
    }

    private Optional<String> findParentEmail(Eleve eleve) {
        // Priorité au compte parent lié, sinon l'email renseigné sur la fiche élève
        return parentRepository.findAll().stream()
                .filter(p -> p.getEnfants().stream().anyMatch(e -> e.getId().equals(eleve.getId())))
                .map(Parent::getUtilisateur)
                .map(u -> u.getEmail())
                .findFirst()
                .or(() -> Optional.ofNullable(eleve.getParentEmail()));
    }

    private boolean emailActive() {
        return "true".equalsIgnoreCase(configurationService.getValeur("NOTIFICATIONS_EMAIL_ENABLED"));
    }
}
