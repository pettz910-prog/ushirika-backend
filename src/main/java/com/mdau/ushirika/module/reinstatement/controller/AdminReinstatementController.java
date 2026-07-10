package com.mdau.ushirika.module.reinstatement.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.reinstatement.dto.AdminReinstatementDecision;
import com.mdau.ushirika.module.reinstatement.dto.ReinstatementRequestDto;
import com.mdau.ushirika.module.reinstatement.service.ReinstatementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AdminReinstatementController {

    private final ReinstatementService reinstatementService;

    @GetMapping("/admin/reinstatement")
    public ApiResponse<PagedResponse<ReinstatementRequestDto>> list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReinstatementRequestDto> result = reinstatementService.listAll(status, pageable);
        return ApiResponse.ok(PagedResponse.from(result));
    }

    @PatchMapping("/admin/reinstatement/{id}/approve")
    public ApiResponse<ReinstatementRequestDto> approve(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) AdminReinstatementDecision body) {

        String notes = body != null ? body.adminNotes() : null;
        return ApiResponse.ok("Request approved.", reinstatementService.approve(id, notes));
    }

    @PatchMapping("/admin/reinstatement/{id}/reject")
    public ApiResponse<ReinstatementRequestDto> reject(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) AdminReinstatementDecision body) {

        String notes = body != null ? body.adminNotes() : null;
        return ApiResponse.ok("Request rejected.", reinstatementService.reject(id, notes));
    }
}
