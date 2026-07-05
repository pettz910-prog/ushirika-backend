package com.mdau.ushirika.module.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCredentialsRequest(

        @NotBlank(message = "Current password is required to make credential changes")
        String currentPassword,

        @Email(message = "New email must be a valid email address")
        @Size(max = 255)
        String newEmail,

        @Size(min = 8, max = 128, message = "New password must be at least 8 characters")
        String newPassword
) {}
