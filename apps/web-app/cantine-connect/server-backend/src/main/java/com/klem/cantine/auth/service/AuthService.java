package com.klem.cantine.auth.service;

import com.klem.cantine.auth.dto.AuthResponseDTO;
import com.klem.cantine.auth.dto.LoginRequestDTO;
import com.klem.cantine.auth.entity.Utilisateur;
import com.klem.cantine.auth.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return utilisateurRepository.findByEmailAndActifTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + email));
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        Utilisateur utilisateur = utilisateurRepository.findByEmailAndActifTrue(dto.email())
                .orElseThrow(() -> new BadCredentialsException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(dto.motDePasse(), utilisateur.getMotDePasse())) {
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }

        String token = jwtService.generateToken(utilisateur);
        return AuthResponseDTO.of(token, jwtService.getExpirationMs(), utilisateur);
    }
}
