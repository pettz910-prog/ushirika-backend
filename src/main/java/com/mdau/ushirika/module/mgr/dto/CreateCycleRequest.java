package com.mdau.ushirika.module.mgr.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCycleRequest(
        @NotBlank String name,

        @NotNull Integer year,

        @NotNull LocalDate startDate,

        /** Max members in this cycle (min 2, default 24). */
        @Min(2) @Max(200)
        Integer totalSlots,

        /** Monthly contribution per member. */
        @DecimalMin("1.00")
        BigDecimal monthlyContribution,

        /** How many beneficiaries are drawn per month. */
        @Min(1)
        Integer payoutsPerMonth,

        /** Fixed payout per beneficiary per draw. */
        @DecimalMin("0.01")
        BigDecimal payoutAmountPerSlot,

        /** % of pool retained as reserve (0–100). */
        @DecimalMin("0") @DecimalMax("100")
        BigDecimal reservePercentage,

        /** Day of month (1–28) the admin runs the monthly draw. */
        @Min(1) @Max(28)
        Integer benefitPayoutDay,

        String notes
) {}
