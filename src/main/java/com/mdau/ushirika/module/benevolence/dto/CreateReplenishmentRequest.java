package com.mdau.ushirika.module.benevolence.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateReplenishmentRequest(
        UUID claimId,
        @NotNull LocalDate dueDate,
        String notes
) {}
