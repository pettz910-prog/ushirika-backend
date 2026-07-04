package com.mdau.ushirika.module.mgr.dto;

import com.mdau.ushirika.module.mgr.entity.MgrSlot;
import com.mdau.ushirika.module.mgr.enums.SlotStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MgrSlotDto(
        UUID id,
        UUID cycleId,
        UUID userId,
        String memberName,
        String email,
        String memberId,
        int slotNumber,
        int payoutMonth,
        int payoutOrder,
        LocalDate scheduledPayoutDate,
        BigDecimal payoutAmount,
        SlotStatus status,
        LocalDateTime paidAt,
        String paymentReference,
        String adminNotes,
        List<MgrContributionDto> contributions
) {
    public static MgrSlotDto from(MgrSlot s, String memberId, List<MgrContributionDto> contributions) {
        String fullName = s.getUser().getFirstName() + " " + s.getUser().getLastName();
        return new MgrSlotDto(
                s.getId(), s.getCycle().getId(), s.getUser().getId(),
                fullName, s.getUser().getEmail(), memberId,
                s.getSlotNumber(), s.getPayoutMonth(), s.getPayoutOrder(),
                s.getScheduledPayoutDate(), s.getPayoutAmount(), s.getStatus(),
                s.getPaidAt(), s.getPaymentReference(), s.getAdminNotes(),
                contributions
        );
    }

    public static MgrSlotDto summary(MgrSlot s, String memberId) {
        String fullName = s.getUser().getFirstName() + " " + s.getUser().getLastName();
        return new MgrSlotDto(
                s.getId(), s.getCycle().getId(), s.getUser().getId(),
                fullName, s.getUser().getEmail(), memberId,
                s.getSlotNumber(), s.getPayoutMonth(), s.getPayoutOrder(),
                s.getScheduledPayoutDate(), s.getPayoutAmount(), s.getStatus(),
                s.getPaidAt(), s.getPaymentReference(), s.getAdminNotes(),
                null
        );
    }
}
