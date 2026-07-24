package com.klem.cantine.auth.controller;

import com.klem.cantine.auth.dto.LoginRequestDTO;
import com.klem.cantine.auth.service.AuthService;
import com.klem.cantine.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequestDTO dto) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(authService.login(dto)));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "UNAUTHORIZED", "Email ou mot de passe incorrect"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(userDetails.getUsername()));
    }
}
