package com.mdau.ushirika.module.member.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AdditionalInfoRequest(

        @NotEmpty(message = "At least one document is required")
        List<String> documentUrls
) {}
