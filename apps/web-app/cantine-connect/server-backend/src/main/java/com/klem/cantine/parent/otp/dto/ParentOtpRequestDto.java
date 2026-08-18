package com.klem.cantine.parent.otp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * {@code email} sert à la fois de canal d'envoi du code et, si ce numéro ne correspond à
 * aucun compte parent existant, de coordonnée pour créer le compte à la vérification du code
 * (voir {@code ParentOtpService#verifierOtp}).
 */
public record ParentOtpRequestDto(

    @NotBlank(message = "Le numéro WhatsApp/téléphone est obligatoire")
    String whatsappNumber,

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    String email
) {}
