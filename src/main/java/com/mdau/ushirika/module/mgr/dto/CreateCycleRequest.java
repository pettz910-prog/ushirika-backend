package com.mdau.ushirika.module.mgr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateCycleRequest(
        @NotBlank String name,
        @NotNull Integer year,
        @NotNull LocalDate startDate,
        String notes
) {}
