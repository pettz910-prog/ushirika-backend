package com.mdau.ushirika.module.scholarship.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record PublicInquiryRequest(

        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email address")
        String email,

        String phone,

        @NotBlank(message = "Message is required")
        String message,

        /** Optional — which program they are inquiring about. */
        UUID programId
) {}
