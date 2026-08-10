package com.klem.coreapi.identity.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InviteUserRequest(

        @NotBlank(message = "must not be blank")
        @Email(message = "must be a valid email address")
        String email,

        @Size(max = 200, message = "must be at most 200 characters")
        String displayName
) {
}
