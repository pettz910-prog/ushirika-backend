package com.mdau.ushirika.module.payment.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.payment.dto.PeerPaymentDto;
import com.mdau.ushirika.module.payment.dto.RejectPeerPaymentRequest;
import com.mdau.ushirika.module.payment.dto.VerifyPeerPaymentRequest;
import com.mdau.ushirika.module.payment.enums.PeerPaymentStatus;
import com.mdau.ushirika.module.payment.service.PeerPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/peer-payments")
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','FINANCIAL_ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Peer Payments — Admin",
     description = "Verify or reject member-reported Zelle / Venmo / CashApp payments")
public class AdminPeerPaymentController {

    private final PeerPaymentService peerPaymentService;

    @GetMapping
    @Operation(summary = "List all peer payment reports, optionally filtered by status")
    public ResponseEntity<ApiResponse<PagedResponse<PeerPaymentDto>>> list(
            @RequestParam(required = false) PeerPaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok("Reports retrieved",
                peerPaymentService.listAll(status,
                    PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @PatchMapping("/{id}/verify")
    @Operation(summary = "Verify a peer payment — admin enters their received TX reference. " +
                         "Must match the member's reference to succeed.")
    public ResponseEntity<ApiResponse<PeerPaymentDto>> verify(
            @PathVariable UUID id,
            @Valid @RequestBody VerifyPeerPaymentRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Payment verified and contribution recorded.", peerPaymentService.verify(id, req)));
    }

    @PatchMapping("/{id}/reject")
    @Operation(summary = "Reject a peer payment report — member is notified with reason and re-entry link")
    public ResponseEntity<ApiResponse<PeerPaymentDto>> reject(
            @PathVariable UUID id,
            @Valid @RequestBody RejectPeerPaymentRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Payment report rejected. Member has been notified.", peerPaymentService.reject(id, req)));
    }
}
