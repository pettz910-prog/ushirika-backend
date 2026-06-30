package com.mdau.ushirika.module.payment.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.payment.dto.PeerPaymentDto;
import com.mdau.ushirika.module.payment.dto.ReportPeerPaymentRequest;
import com.mdau.ushirika.module.payment.service.PeerPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/peer-payments")
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Peer Payments — Member",
     description = "Member self-report of Zelle / Venmo / CashApp contributions")
public class PeerPaymentController {

    private final PeerPaymentService peerPaymentService;

    @PostMapping("/report")
    @Operation(summary = "Report a Zelle / Venmo / CashApp payment after sending it")
    public ResponseEntity<ApiResponse<PeerPaymentDto>> report(
            @Valid @RequestBody ReportPeerPaymentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Payment report submitted. It will be verified shortly.",
                      peerPaymentService.report(req)));
    }

    @GetMapping("/my")
    @Operation(summary = "My peer payment reports and their verification status")
    public ResponseEntity<ApiResponse<PagedResponse<PeerPaymentDto>>> myReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok("Reports retrieved",
                peerPaymentService.myReports(
                    PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }
}
