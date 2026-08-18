package com.klem.cantine.parent.otp.dto;

import jakarta.validation.constraints.NotBlank;

public record ParentOtpVerifyDto(

    @NotBlank(message = "Le numéro WhatsApp/téléphone est obligatoire")
    String whatsappNumber,

    @NotBlank(message = "Le code de vérification est obligatoire")
    String otpCode
) {}
