package com.mdau.ushirika.module.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyOnboardingEmailRequest(

        @NotBlank(message = "Verification code is required")
        @Size(min = 6, max = 6, message = "Verification code must be 6 digits")
        String otp
) {}
