package com.mdau.ushirika.module.loan.dto;

import com.mdau.ushirika.module.loan.entity.LoanGuarantor;
import com.mdau.ushirika.module.loan.enums.GuarantorStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoanGuarantorDto(
        UUID id,
        UUID guarantorUserId,
        String guarantorName,
        String guarantorEmail,
        String guarantorMemberId,
        GuarantorStatus status,
        LocalDateTime respondedAt,
        String notes
) {
    public static LoanGuarantorDto from(LoanGuarantor g, String memberId) {
        return new LoanGuarantorDto(
                g.getId(),
                g.getGuarantorUser().getId(),
                g.getGuarantorUser().getFullName(),
                g.getGuarantorUser().getEmail(),
                memberId,
                g.getStatus(),
                g.getRespondedAt(),
                g.getNotes()
        );
    }
}
