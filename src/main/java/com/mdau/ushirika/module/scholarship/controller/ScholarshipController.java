package com.mdau.ushirika.module.scholarship.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.scholarship.dto.*;
import com.mdau.ushirika.module.scholarship.service.ScholarshipService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Scholarships — Member/Public", description = "Apply for and track scholarship programs")
public class ScholarshipController {

    private final ScholarshipService scholarshipService;

    // ─── Public

    @GetMapping("/public/scholarships/programs")
    @Operation(summary = "List currently OPEN scholarship programs")
    public ResponseEntity<ApiResponse<List<ScholarshipProgramDto>>> openPrograms() {
        return ResponseEntity.ok(ApiResponse.ok("Programs retrieved", scholarshipService.listOpenPrograms()));
    }

    @PostMapping("/public/scholarships/inquire")
    @Operation(summary = "Submit a public scholarship inquiry (no account required)")
    public ResponseEntity<ApiResponse<Void>> inquire(@Valid @RequestBody PublicInquiryRequest req) {
        scholarshipService.submitInquiry(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Your inquiry has been received. We will be in touch shortly."));
    }

    // ─── Member (must be an approved member with a memberId)

    @PostMapping("/scholarships/applications")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Apply for a scholarship program (approved members only)")
    public ResponseEntity<ApiResponse<ScholarshipApplicationTrackDto>> apply(
            @Valid @RequestBody ScholarshipApplicationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Application saved", scholarshipService.apply(req)));
    }

    @PostMapping("/scholarships/applications/{id}/submit")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Submit a DRAFT scholarship application to the review board")
    public ResponseEntity<ApiResponse<ScholarshipApplicationTrackDto>> submit(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Application submitted for review",
                scholarshipService.submitApplication(id)));
    }

    @GetMapping("/scholarships/applications/my")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "List all my scholarship applications")
    public ResponseEntity<ApiResponse<PagedResponse<ScholarshipApplicationTrackDto>>> myApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Applications retrieved",
                scholarshipService.myApplications(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/scholarships/applications/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get a single scholarship application (own only)")
    public ResponseEntity<ApiResponse<ScholarshipApplicationTrackDto>> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Application retrieved", scholarshipService.myApplication(id)));
    }
}
