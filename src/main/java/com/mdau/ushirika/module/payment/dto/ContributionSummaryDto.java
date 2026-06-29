package com.mdau.ushirika.module.payment.dto;

import java.math.BigDecimal;

public record ContributionSummaryDto(
        BigDecimal totalPaid,
        String currency,
        long totalPayments
) {}
