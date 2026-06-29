package com.mdau.ushirika.module.welfare.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.welfare.dto.WelfareCategoryDto;
import com.mdau.ushirika.module.welfare.dto.WelfareRequestRequest;
import com.mdau.ushirika.module.welfare.dto.WelfareRequestTrackDto;
import com.mdau.ushirika.module.welfare.service.WelfareService;
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
@Tag(name = "Welfare — Member", description = "Submit and track welfare/benevolence requests")
public class WelfareController {

    private final WelfareService welfareService;

    // ─── Public — category listing (members need to see this before applying)

    @GetMapping("/public/welfare/categories")
    @Operation(summary = "List active welfare categories (public)")
    public ResponseEntity<ApiResponse<List<WelfareCategoryDto>>> publicCategories() {
        return ResponseEntity.ok(ApiResponse.ok("Categories retrieved", welfareService.listActiveCategories()));
    }

    // ─── Member

    @PostMapping("/welfare/requests")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN') or hasRole('SUPERADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new welfare request (saved as DRAFT)")
    public ResponseEntity<ApiResponse<WelfareRequestTrackDto>> create(
            @Valid @RequestBody WelfareRequestRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Welfare request created", welfareService.saveRequest(req)));
    }

    @PostMapping("/welfare/requests/{id}/submit")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Submit a DRAFT welfare request to the review board")
    public ResponseEntity<ApiResponse<WelfareRequestTrackDto>> submit(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Request submitted for review", welfareService.submitRequest(id)));
    }

    @GetMapping("/welfare/requests/my")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "List all my welfare requests")
    public ResponseEntity<ApiResponse<PagedResponse<WelfareRequestTrackDto>>> myRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Requests retrieved",
                welfareService.myRequests(PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/welfare/requests/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get a single welfare request (own only)")
    public ResponseEntity<ApiResponse<WelfareRequestTrackDto>> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Request retrieved", welfareService.myRequest(id)));
    }
}
