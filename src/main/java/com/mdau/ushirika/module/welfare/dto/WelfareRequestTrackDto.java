package com.mdau.ushirika.module.welfare.dto;

import com.mdau.ushirika.module.welfare.entity.WelfareRequest;
import com.mdau.ushirika.module.welfare.enums.WelfareRequestStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/** Safe member/public view — no admin names, no internal notes. */
public record WelfareRequestTrackDto(
        UUID id,
        String referenceNumber,
        String categoryName,
        WelfareRequestStatus status,
        BigDecimal amountRequested,
        String currency,
        String description,
        String rejectionReason,
        LocalDateTime submittedAt,
        LocalDateTime reviewedAt,
        LocalDateTime approvedAt,
        DisbursementSummary disbursement
) {

    public record DisbursementSummary(
            BigDecimal amountDisbursed,
            String currency,
            String method,
            LocalDateTime disbursedAt
    ) {}

    public static WelfareRequestTrackDto from(WelfareRequest r) {
        DisbursementSummary ds = null;
        if (r.getDisbursement() != null) {
            var d = r.getDisbursement();
            ds = new DisbursementSummary(
                    d.getAmountDisbursed(), d.getCurrency(),
                    d.getMethod().name(), d.getDisbursedAt()
            );
        }
        return new WelfareRequestTrackDto(
                r.getId(), r.getReferenceNumber(),
                r.getCategory().getName(),
                r.getStatus(), r.getAmountRequested(), r.getCurrency(),
                r.getDescription(), r.getRejectionReason(),
                r.getSubmittedAt(), r.getReviewedAt(), r.getApprovedAt(), ds
        );
    }
}
