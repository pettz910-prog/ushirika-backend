package com.mdau.ushirika.module.benevolence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SubmitClaimRequest(
        UUID categoryId,
        UUID beneficiaryId,
        @NotBlank String deceasedName,
        @NotBlank String relationship,
        @NotNull LocalDate dateOfDeath,
        @NotBlank String locationOfDeath,
        LocalDate funeralDate,
        String funeralLocation,
        @NotBlank String contactName,
        @NotBlank String contactPhone,
        String description,
        List<String> documentUrls
) {}
