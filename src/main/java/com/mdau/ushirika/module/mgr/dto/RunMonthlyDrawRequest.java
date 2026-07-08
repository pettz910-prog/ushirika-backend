package com.mdau.ushirika.module.mgr.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RunMonthlyDrawRequest(
        @NotNull @Min(1) @Max(12)
        Integer month,

        @Min(2000) @Max(2100)
        Integer year
) {}
