package com.mdau.ushirika.module.scholarship.dto;

import com.mdau.ushirika.module.scholarship.entity.ScholarshipProgram;
import com.mdau.ushirika.module.scholarship.enums.ScholarshipProgramStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ScholarshipProgramDto(
        UUID id,
        String name,
        String description,
        String eligibilityCriteria,
        BigDecimal amountPerRecipient,
        String currency,
        Integer totalSlots,
        String academicYear,
        LocalDate applicationDeadline,
        ScholarshipProgramStatus status
) {
    public static ScholarshipProgramDto from(ScholarshipProgram p) {
        return new ScholarshipProgramDto(
                p.getId(), p.getName(), p.getDescription(),
                p.getEligibilityCriteria(), p.getAmountPerRecipient(),
                p.getCurrency(), p.getTotalSlots(), p.getAcademicYear(),
                p.getApplicationDeadline(), p.getStatus()
        );
    }
}
