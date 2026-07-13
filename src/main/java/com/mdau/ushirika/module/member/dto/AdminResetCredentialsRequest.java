package com.mdau.ushirika.module.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Superadmin-only credential override.
 * At least one of newEmail or newPassword must be provided.
 * No current-password check — superadmin authority is sufficient.
 */
public record AdminResetCredentialsRequest(

        @Email(message = "newEmail must be a valid email address")
        @Size(max = 255)
        String newEmail,

        @Size(min = 8, max = 128, message = "newPassword must be at least 8 characters")
        String newPassword
) {}
