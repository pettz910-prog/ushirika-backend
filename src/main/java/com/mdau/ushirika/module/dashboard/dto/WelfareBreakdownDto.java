package com.mdau.ushirika.module.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public record WelfareBreakdownDto(
        List<CategoryRow> byCategory,
        BigDecimal totalDisbursed
) {
    public record CategoryRow(
            String categoryName,
            long total,
            long approved,
            long disbursed,
            long rejected,
            long pending
    ) {}
}
