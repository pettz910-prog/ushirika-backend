package com.mdau.ushirika.module.donation.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.donation.dto.*;
import com.mdau.ushirika.module.donation.service.DonationService;
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

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Donations — Member/Public", description = "Campaign listings and donation payment flows")
public class DonationController {

    private final DonationService donationService;

    // ─── Public

    @GetMapping("/public/donations/campaigns")
    @Operation(summary = "List active public donation campaigns")
    public ResponseEntity<ApiResponse<PagedResponse<CampaignDto>>> publicCampaigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Campaigns retrieved",
                donationService.listPublicCampaigns(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/public/donations/campaigns/{id}")
    @Operation(summary = "Get a single active public campaign with total raised")
    public ResponseEntity<ApiResponse<CampaignDto>> publicCampaign(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Campaign retrieved", donationService.getPublicCampaign(id)));
    }

    @PostMapping("/public/donations/initialize")
    @Operation(summary = "Initialize a donation as a guest (no account required)")
    public ResponseEntity<ApiResponse<DonationInitResponse>> guestInit(
            @Valid @RequestBody DonationInitRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Donation initialized", donationService.initializeGuest(req)));
    }

    // ─── Member

    @PostMapping("/donations/initialize")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Initialize a donation as a logged-in member")
    public ResponseEntity<ApiResponse<DonationInitResponse>> memberInit(
            @Valid @RequestBody DonationInitRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Donation initialized", donationService.initializeMember(req)));
    }

    @GetMapping("/donations/my")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "List my donation history")
    public ResponseEntity<ApiResponse<PagedResponse<DonationDto>>> myDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Donations retrieved",
                donationService.myDonations(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }
}
