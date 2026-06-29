package com.mdau.ushirika.module.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

/** Monthly time-series data for financial reports and charts. */
public record MonthlySeriesDto(
        List<MonthlyPoint> contributions,
        List<MonthlyPoint> donations
) {
    public record MonthlyPoint(
            int year,
            int month,
            String label,       // "Jan 2025"
            BigDecimal amount,
            long count
    ) {}
}
