package com.mdau.ushirika.module.attendance.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.attendance.dto.AttendanceSummaryDto;
import com.mdau.ushirika.module.attendance.dto.FineDto;
import com.mdau.ushirika.module.attendance.service.FineService;
import com.mdau.ushirika.module.attendance.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberAttendanceController {

    private final MeetingService meetingService;
    private final FineService fineService;

    @GetMapping("/attendance/my")
    public ApiResponse<AttendanceSummaryDto> myAttendance() {
        return ApiResponse.ok(meetingService.getMyAttendanceSummary());
    }

    @GetMapping("/fines/my")
    public ApiResponse<List<FineDto>> myFines() {
        return ApiResponse.ok(fineService.getMyFines());
    }
}
