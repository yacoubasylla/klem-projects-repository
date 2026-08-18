package com.klem.cantine.parent.otp.dto;

import jakarta.validation.constraints.NotBlank;

public record ParentOtpRequestDto(

    @NotBlank(message = "Le numéro WhatsApp/téléphone est obligatoire")
    String whatsappNumber
) {}
