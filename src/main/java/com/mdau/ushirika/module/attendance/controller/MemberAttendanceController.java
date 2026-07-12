package com.mdau.ushirika.module.attendance.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.attendance.dto.*;
import com.mdau.ushirika.module.attendance.service.FinePaymentService;
import com.mdau.ushirika.module.attendance.service.FineService;
import com.mdau.ushirika.module.attendance.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MemberAttendanceController {

    private final MeetingService meetingService;
    private final FineService fineService;
    private final FinePaymentService finePaymentService;

    @GetMapping("/attendance/my")
    public ApiResponse<AttendanceSummaryDto> myAttendance() {
        return ApiResponse.ok(meetingService.getMyAttendanceSummary());
    }

    @GetMapping("/fines/my")
    public ApiResponse<List<FineDto>> myFines() {
        return ApiResponse.ok(fineService.getMyFines());
    }

    // ── Fine payment submission (two-sided verification) ──────────────────────

    @PostMapping("/fines/my/{fineId}/payment")
    public ResponseEntity<ApiResponse<FinePaymentDto>> submitFinePayment(
            @PathVariable UUID fineId,
            @Valid @RequestBody SubmitFinePaymentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Payment submitted for verification",
                        finePaymentService.submit(fineId, req)));
    }

    @GetMapping("/fines/my/payments")
    public ApiResponse<List<FinePaymentDto>> myFinePayments() {
        return ApiResponse.ok(finePaymentService.myPayments());
    }
}
