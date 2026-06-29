package com.mdau.ushirika.module.manualpayment.controller;

import com.mdau.ushirika.module.manualpayment.dto.DelegatePermissionRequest;
import com.mdau.ushirika.module.manualpayment.dto.FinancialOfficialPermissionDto;
import com.mdau.ushirika.module.manualpayment.service.ManualPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Delegation management — FINANCIAL_ADMIN only.
 * Allows a Financial Admin to grant or revoke recording/approval rights for Financial Officials.
 */
@RestController
@RequestMapping("/financial/admin/officials")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FINANCIAL_ADMIN')")
public class FinancialAdminController {

    private final ManualPaymentService service;

    /** List all Financial Officials and their current permission state. */
    @GetMapping("/permissions")
    public List<FinancialOfficialPermissionDto> listPermissions() {
        return service.listAllPermissions();
    }

    /**
     * Grant or update permissions for a specific official.
     * Pass canRecordPayments and/or canApprovePayments as true/false.
     * Creates the record if it doesn't exist; updates it if it does.
     */
    @PutMapping("/{officialId}/permissions")
    public FinancialOfficialPermissionDto setPermissions(
            @PathVariable UUID officialId,
            @RequestBody DelegatePermissionRequest req) {
        return service.setPermissions(officialId, req);
    }

    /** Revoke all delegated permissions for an official (removes the record entirely). */
    @DeleteMapping("/{officialId}/permissions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokePermissions(@PathVariable UUID officialId) {
        service.revokePermissions(officialId);
    }
}
