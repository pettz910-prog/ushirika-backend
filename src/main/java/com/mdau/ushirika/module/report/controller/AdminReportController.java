package com.mdau.ushirika.module.report.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.report.dto.AttendanceComplianceReport;
import com.mdau.ushirika.module.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;

    @GetMapping("/admin/reports/members.csv")
    public ResponseEntity<byte[]> membersCsv() {
        return csv(reportService.membersCsv(), "members_" + LocalDate.now() + ".csv");
    }

    @GetMapping("/admin/reports/dues.csv")
    public ResponseEntity<byte[]> duesCsv(@RequestParam(required = false) Integer year) {
        String filename = year != null
                ? "dues_" + year + ".csv"
                : "dues_all_" + LocalDate.now() + ".csv";
        return csv(reportService.duesCsv(year), filename);
    }

    @GetMapping("/admin/reports/fines.csv")
    public ResponseEntity<byte[]> finesCsv() {
        return csv(reportService.finesCsv(), "fines_" + LocalDate.now() + ".csv");
    }

    @GetMapping("/admin/reports/attendance.csv")
    public ResponseEntity<byte[]> attendanceCsv() {
        return csv(reportService.attendanceCsv(), "attendance_compliance_" + LocalDate.now() + ".csv");
    }

    @GetMapping("/admin/reports/attendance")
    public ApiResponse<AttendanceComplianceReport> attendanceReport() {
        return ApiResponse.ok(reportService.attendanceReport());
    }

    @GetMapping("/admin/reports/mgr.csv")
    public ResponseEntity<byte[]> mgrCsv() {
        return csv(reportService.mgrCsv(), "mgr_contributions_" + LocalDate.now() + ".csv");
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private static ResponseEntity<byte[]> csv(byte[] data, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .contentLength(data.length)
                .body(data);
    }
}
