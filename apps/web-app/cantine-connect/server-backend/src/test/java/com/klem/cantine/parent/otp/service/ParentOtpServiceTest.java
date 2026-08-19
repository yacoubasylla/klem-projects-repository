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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParentOtpServiceTest {

    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private ParentRepository parentRepository;
    @Mock private OtpStore otpStore;
    @Mock private NotificationSender smsSender;
    @Mock private NotificationSender whatsappSender;
    @Mock private NotificationSender emailSender;
    @Mock private ConfigurationService configurationService;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        lenient().when(smsSender.getCanal()).thenReturn("SMS");
        lenient().when(whatsappSender.getCanal()).thenReturn("WHATSAPP");
        lenient().when(emailSender.getCanal()).thenReturn("EMAIL");
    }

    private ParentOtpService service() {
        return new ParentOtpService(
                utilisateurRepository, parentRepository, otpStore,
                List.of(smsSender, whatsappSender, emailSender), configurationService,
                jwtService, passwordEncoder);
    }

    private Utilisateur parent(String telephone) {
        return Utilisateur.builder()
                .id(1L).nom("Kone").prenom("Awa").email("awa@example.com")
                .telephone(telephone).role(Role.PARENT).actif(true)
                .build();
    }

    @Test
    void envoyerOtp_parDefaut_utiliseWhatsappEtEmail() {
        Utilisateur u = parent("+2250700000001");
        when(utilisateurRepository.findByTelephoneAndRoleAndActifTrue("+2250700000001", Role.PARENT))
                .thenReturn(Optional.of(u));
        when(configurationService.getValeur(ParentOtpService.CLE_CANAL_TELEPHONE)).thenReturn("WHATSAPP");

        service().envoyerOtp("0700000001", "autre@example.com");

        var codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(otpStore).enregistrer(eq("+2250700000001"), codeCaptor.capture(), eq("autre@example.com"));
        assertThat(codeCaptor.getValue()).matches("\\d{6}");
        verify(whatsappSender).envoyer(eq("+2250700000001"), anyString(), anyString());
        verify(emailSender).envoyer(eq("awa@example.com"), anyString(), anyString());
        verify(smsSender, never()).envoyer(anyString(), anyString(), anyString());
    }

    @Test
    void envoyerOtp_canalConfigureEnSms_utiliseSmsPasWhatsapp() {
        when(utilisateurRepository.findByTelephoneAndRoleAndActifTrue(anyString(), eq(Role.PARENT)))
                .thenReturn(Optional.empty());
        when(configurationService.getValeur(ParentOtpService.CLE_CANAL_TELEPHONE)).thenReturn("SMS");

        service().envoyerOtp("0700000099", "nouveau@example.com");

        verify(smsSender).envoyer(eq("+2250700000099"), anyString(), anyString());
        verify(whatsappSender, never()).envoyer(anyString(), anyString(), anyString());
        verify(emailSender).envoyer(eq("nouveau@example.com"), anyString(), anyString());
    }

    @Test
    void verifierOtp_compteExistant_retourneUnJetonDeSessionSansCreerDeCompte() {
        Utilisateur u = parent("+2250700000001");
        when(otpStore.verifierEtInvalider("+2250700000001", "123456")).thenReturn(Optional.of("awa@example.com"));
        when(utilisateurRepository.findByTelephoneAndRoleAndActifTrue("+2250700000001", Role.PARENT))
                .thenReturn(Optional.of(u));
        when(jwtService.generateToken(u)).thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(3_600_000L);

        AuthResponseDTO response = service().verifierOtp("0700000001", "123456");

        assertThat(response.token()).isEqualTo("jwt-token");
        verify(utilisateurRepository, never()).save(any());
        verify(parentRepository, never()).save(any());
    }

    @Test
    void verifierOtp_numeroSansCompte_creeLeCompteParentEtLeProfil() {
        when(otpStore.verifierEtInvalider("+2250700000099", "123456"))
                .thenReturn(Optional.of("nouveau@example.com"));
        when(utilisateurRepository.findByTelephoneAndRoleAndActifTrue("+2250700000099", Role.PARENT))
                .thenReturn(Optional.empty());
        when(utilisateurRepository.existsByEmail("nouveau@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(utilisateurRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(3_600_000L);

        AuthResponseDTO response = service().verifierOtp("0700000099", "123456");

        assertThat(response.token()).isEqualTo("jwt-token");
        var utilisateurCaptor = ArgumentCaptor.forClass(Utilisateur.class);
        verify(utilisateurRepository).save(utilisateurCaptor.capture());
        assertThat(utilisateurCaptor.getValue().getTelephone()).isEqualTo("+2250700000099");
        assertThat(utilisateurCaptor.getValue().getEmail()).isEqualTo("nouveau@example.com");
        assertThat(utilisateurCaptor.getValue().getRole()).isEqualTo(Role.PARENT);

        var parentCaptor = ArgumentCaptor.forClass(Parent.class);
        verify(parentRepository).save(parentCaptor.capture());
        assertThat(parentCaptor.getValue().getUtilisateur()).isEqualTo(utilisateurCaptor.getValue());
    }

    @Test
    void verifierOtp_emailDejaUtiliseParUnAutreCompte_leveIllegalState() {
        when(otpStore.verifierEtInvalider("+2250700000099", "123456"))
                .thenReturn(Optional.of("deja-pris@example.com"));
        when(utilisateurRepository.findByTelephoneAndRoleAndActifTrue("+2250700000099", Role.PARENT))
                .thenReturn(Optional.empty());
        when(utilisateurRepository.existsByEmail("deja-pris@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service().verifierOtp("0700000099", "123456"))
                .isInstanceOf(IllegalStateException.class);

        verify(utilisateurRepository, never()).save(any());
    }

    /**
     * Régression : le "0" initial d'un numéro ivoirien fait partie du numéro d'abonné depuis la
     * réforme de numérotation 2021 — le retirer produit un numéro E.164 invalide, rejeté par
     * Twilio ("No Twilio trial phone number is assigned for messaging to this destination
     * number") car il ne correspond à aucun numéro réel.
     */
    @Test
    void envoyerOtp_numeroIvoirien_conserveLeZeroInitial() {
        when(utilisateurRepository.findByTelephoneAndRoleAndActifTrue(anyString(), eq(Role.PARENT)))
                .thenReturn(Optional.empty());
        when(configurationService.getValeur(ParentOtpService.CLE_CANAL_TELEPHONE)).thenReturn("WHATSAPP");

        service().envoyerOtp("0554025100", "test@example.com");

        verify(whatsappSender).envoyer(eq("+2250554025100"), anyString(), anyString());
    }

    @Test
    void verifierOtp_codeInvalide_leveIllegalArgument() {
        when(otpStore.verifierEtInvalider(eq("+2250700000001"), anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().verifierOtp("0700000001", "000000"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(jwtService, never()).generateToken(any());
    }
}
