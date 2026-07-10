package com.mdau.ushirika.module.report.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.report.dto.DueReceiptDto;
import com.mdau.ushirika.module.report.dto.FineReceiptDto;
import com.mdau.ushirika.module.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReceiptController {

    private final ReportService reportService;

    @GetMapping("/receipts/dues/{dueId}")
    public ApiResponse<DueReceiptDto> dueReceipt(@PathVariable UUID dueId) {
        return ApiResponse.ok(reportService.dueReceipt(dueId));
    }

    @GetMapping("/receipts/fines/{fineId}")
    public ApiResponse<FineReceiptDto> fineReceipt(@PathVariable UUID fineId) {
        return ApiResponse.ok(reportService.fineReceipt(fineId));
    }
}
