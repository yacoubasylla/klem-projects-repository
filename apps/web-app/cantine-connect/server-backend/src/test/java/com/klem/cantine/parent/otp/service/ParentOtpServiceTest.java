package com.klem.cantine.parent.otp.service;

import com.klem.cantine.auth.dto.AuthResponseDTO;
import com.klem.cantine.auth.entity.Role;
import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.auth.repository.UtilisateurRepository;
import com.klem.cantine.auth.service.JwtService;
import com.klem.cantine.notification.NotificationDispatcher;
import com.klem.cantine.parent.otp.OtpStore;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParentOtpServiceTest {

    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private OtpStore otpStore;
    @Mock private NotificationDispatcher notificationDispatcher;
    @Mock private JwtService jwtService;

    private ParentOtpService service() {
        return new ParentOtpService(utilisateurRepository, otpStore, notificationDispatcher, jwtService);
    }

    private Utilisateur parent(String telephone) {
        return Utilisateur.builder()
                .id(1L).nom("Kone").prenom("Awa").email("awa@example.com")
                .telephone(telephone).role(Role.PARENT).actif(true)
                .build();
    }

    @Test
    void envoyerOtp_compteParentActifTrouve_genereEtDispatcheLeCode() {
        Utilisateur u = parent("+225700000001");
        when(utilisateurRepository.findByTelephoneAndRoleAndActifTrue("+225700000001", Role.PARENT))
                .thenReturn(Optional.of(u));

        service().envoyerOtp("0700000001");

        var codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(otpStore).enregistrer(eq("+225700000001"), codeCaptor.capture());
        assertThat(codeCaptor.getValue()).matches("\\d{6}");
        verify(notificationDispatcher).envoyer(eq("awa@example.com"), eq("+225700000001"), anyString(), anyString());
    }

    @Test
    void envoyerOtp_numeroSansCompteParentActif_leveEntityNotFound() {
        when(utilisateurRepository.findByTelephoneAndRoleAndActifTrue(anyString(), eq(Role.PARENT)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().envoyerOtp("0700000099"))
                .isInstanceOf(EntityNotFoundException.class);

        verify(otpStore, never()).enregistrer(anyString(), anyString());
    }

    @Test
    void verifierOtp_codeValide_retourneUnJetonDeSession() {
        Utilisateur u = parent("+225700000001");
        when(otpStore.verifierEtInvalider("+225700000001", "123456")).thenReturn(true);
        when(utilisateurRepository.findByTelephoneAndRoleAndActifTrue("+225700000001", Role.PARENT))
                .thenReturn(Optional.of(u));
        when(jwtService.generateToken(u)).thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(3_600_000L);

        AuthResponseDTO response = service().verifierOtp("0700000001", "123456");

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.role()).isEqualTo("PARENT");
    }

    @Test
    void verifierOtp_codeInvalide_leveIllegalArgument() {
        when(otpStore.verifierEtInvalider(eq("+225700000001"), anyString())).thenReturn(false);

        assertThatThrownBy(() -> service().verifierOtp("0700000001", "000000"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(jwtService, never()).generateToken(any());
    }
}
