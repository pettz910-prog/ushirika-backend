package com.mdau.ushirika.module.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public record ScholarshipBreakdownDto(
        List<ProgramRow> byProgram,
        BigDecimal totalAwarded
) {
    public record ProgramRow(
            String programName,
            String academicYear,
            long totalApplications,
            long approved,
            long awarded,
            long rejected,
            long pending
    ) {}
}
