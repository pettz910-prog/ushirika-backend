package com.mdau.ushirika.module.manualpayment.dto;

import com.mdau.ushirika.module.manualpayment.entity.FinancialOfficialPermission;

import java.time.LocalDateTime;
import java.util.UUID;

public record FinancialOfficialPermissionDto(
    UUID id,
    UUID officialId,
    String officialName,
    String officialEmail,
    boolean canRecordPayments,
    boolean canApprovePayments,
    String grantedByName,
    String grantedByEmail,
    LocalDateTime grantedAt
) {
    public static FinancialOfficialPermissionDto from(FinancialOfficialPermission p) {
        return new FinancialOfficialPermissionDto(
            p.getId(),
            p.getOfficial().getId(),
            p.getOfficial().getFullName(),
            p.getOfficial().getEmail(),
            p.isCanRecordPayments(),
            p.isCanApprovePayments(),
            p.getGrantedBy().getFullName(),
            p.getGrantedBy().getEmail(),
            p.getCreatedAt()
        );
    }
}
