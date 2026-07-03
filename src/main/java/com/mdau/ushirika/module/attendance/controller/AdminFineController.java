package com.mdau.ushirika.module.attendance.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.attendance.dto.CreateFineRequest;
import com.mdau.ushirika.module.attendance.dto.FineDto;
import com.mdau.ushirika.module.attendance.service.FineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/fines")
@RequiredArgsConstructor
public class AdminFineController {

    private final FineService fineService;

    @GetMapping
    public ApiResponse<Page<FineDto>> list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ApiResponse.ok(fineService.listFines(status, PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FineDto>> create(@Valid @RequestBody CreateFineRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Fine issued.", fineService.createFine(req)));
    }

    @GetMapping("/member/{userId}")
    public ApiResponse<List<FineDto>> forMember(@PathVariable UUID userId) {
        return ApiResponse.ok(fineService.getFinesForMember(userId));
    }

    @PatchMapping("/{id}/waive")
    public ApiResponse<FineDto> waive(@PathVariable UUID id,
                                       @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        return ApiResponse.ok("Fine waived.", fineService.waiveFine(id, reason));
    }

    @PatchMapping("/{id}/pay")
    public ApiResponse<FineDto> markPaid(@PathVariable UUID id) {
        return ApiResponse.ok("Fine marked as paid.", fineService.markPaid(id));
    }
}
