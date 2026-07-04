package com.mdau.ushirika.module.mgr.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignSlotRequest(
        @NotNull UUID userId,
        @NotNull @Min(1) @Max(24) Integer slotNumber
) {}
