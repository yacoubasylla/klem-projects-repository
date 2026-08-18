package com.klem.cantine.parent.otp.controller;

import com.klem.cantine.common.ApiResponse;
import com.klem.cantine.parent.otp.dto.ParentOtpRequestDto;
import com.klem.cantine.parent.otp.dto.ParentOtpVerifyDto;
import com.klem.cantine.parent.otp.service.ParentOtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Connexion parent par OTP (WhatsApp/SMS/Email) — public, sans JWT (voir SecurityConfig).
 * Ne crée jamais de compte : un numéro sans compte PARENT actif reçoit une erreur explicite
 * (404) invitant à soumettre une demande d'accès (`POST /api/v1/demandes-acces`).
 */
@RestController
@RequestMapping("/api/v1/parents/otp")
@RequiredArgsConstructor
public class ParentOtpController {

    private final ParentOtpService parentOtpService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<?>> envoyer(@Valid @RequestBody ParentOtpRequestDto dto) {
        parentOtpService.envoyerOtp(dto.whatsappNumber());
        return ResponseEntity.ok(ApiResponse.ok("Code de vérification envoyé", null));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verifier(@Valid @RequestBody ParentOtpVerifyDto dto) {
        return ResponseEntity.ok(
                ApiResponse.ok(parentOtpService.verifierOtp(dto.whatsappNumber(), dto.otpCode())));
    }
}
