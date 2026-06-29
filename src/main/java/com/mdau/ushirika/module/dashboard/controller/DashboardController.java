package com.mdau.ushirika.module.dashboard.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.dashboard.dto.DashboardSummaryDto;
import com.mdau.ushirika.module.dashboard.dto.MonthlySeriesDto;
import com.mdau.ushirika.module.dashboard.dto.ScholarshipBreakdownDto;
import com.mdau.ushirika.module.dashboard.dto.WelfareBreakdownDto;
import com.mdau.ushirika.module.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Dashboard & Reports", description = "KPI summary and financial reports for the admin portal")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin/dashboard")
    @Operation(summary = "Full KPI snapshot — members, welfare, scholarships, finance, events, content, notifications")
    public ResponseEntity<ApiResponse<DashboardSummaryDto>> dashboard() {
        return ResponseEntity.ok(ApiResponse.ok("Dashboard loaded", dashboardService.getDashboard()));
    }

    @GetMapping("/admin/reports/financial")
    @Operation(summary = "Monthly contributions and donations for the last N months (default 12)",
               description = "Returns two time-series arrays: contributions and donations. Suitable for bar/line charts.")
    public ResponseEntity<ApiResponse<MonthlySeriesDto>> financialReport(
            @RequestParam(defaultValue = "12") int months
    ) {
        if (months < 1 || months > 60) months = 12;
        return ResponseEntity.ok(ApiResponse.ok("Financial report generated",
                dashboardService.getFinancialSeries(months)));
    }

    @GetMapping("/admin/reports/welfare")
    @Operation(summary = "Welfare requests broken down by category and status")
    public ResponseEntity<ApiResponse<WelfareBreakdownDto>> welfareReport() {
        return ResponseEntity.ok(ApiResponse.ok("Welfare report generated",
                dashboardService.getWelfareBreakdown()));
    }

    @GetMapping("/admin/reports/scholarships")
    @Operation(summary = "Scholarship applications broken down by program and status")
    public ResponseEntity<ApiResponse<ScholarshipBreakdownDto>> scholarshipReport() {
        return ResponseEntity.ok(ApiResponse.ok("Scholarship report generated",
                dashboardService.getScholarshipBreakdown()));
    }
}
