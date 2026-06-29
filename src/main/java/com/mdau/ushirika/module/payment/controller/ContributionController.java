package com.mdau.ushirika.module.payment.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.payment.dto.*;
import com.mdau.ushirika.module.payment.service.ContributionService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Contributions — Member", description = "Member dues and contribution payments")
public class ContributionController {

    private final ContributionService contributionService;

    // ---------------------------------------------------------------- Public — plans

    @GetMapping("/public/contributions/plans")
    @Operation(summary = "List active contribution plans (public)")
    public ResponseEntity<ApiResponse<List<ContributionPlanDto>>> activePlans() {
        return ResponseEntity.ok(ApiResponse.ok("Plans retrieved", contributionService.listActivePlans()));
    }

    // ---------------------------------------------------------------- Member

    @PostMapping("/contributions/pay")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Initiate a contribution payment — returns Stripe Checkout URL")
    public ResponseEntity<ApiResponse<PaymentInitDto>> initiate(@Valid @RequestBody InitiateContributionRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Payment initialized", contributionService.initiateContribution(req)));
    }

    @GetMapping("/contributions/my")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "My confirmed contribution history")
    public ResponseEntity<ApiResponse<PagedResponse<MemberContributionDto>>> myContributions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Contributions retrieved",
                contributionService.myContributions(PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/contributions/my/summary")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Total contributions summary for the logged-in member")
    public ResponseEntity<ApiResponse<ContributionSummaryDto>> mySummary() {
        return ResponseEntity.ok(ApiResponse.ok("Summary retrieved", contributionService.mySummary()));
    }
}
