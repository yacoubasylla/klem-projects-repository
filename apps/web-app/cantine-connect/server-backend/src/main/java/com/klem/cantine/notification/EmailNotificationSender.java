package com.klem.cantine.notification;

import com.klem.cantine.parametrage.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationSender implements NotificationSender {

    private final ObjectProvider<MailSender> mailSenderProvider;
    private final ConfigurationService configurationService;

    @Value("${notification.from:noreply@cantine-connect.ci}")
    private String fromAddress;

    @Override
    public String getCanal() {
        return "EMAIL";
    }

    @Override
    public boolean estActif() {
        return "true".equalsIgnoreCase(configurationService.getValeur("NOTIFICATIONS_EMAIL_ENABLED"));
    }

    @Override
    public void envoyer(String destinataire, String sujet, String corps) {
        if (destinataire == null || destinataire.isBlank()) return;
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
}
