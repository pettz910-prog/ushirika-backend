package com.mdau.ushirika.module.manualpayment.dto;

public record DelegatePermissionRequest(
    boolean canRecordPayments,
    boolean canApprovePayments
) {}
