package com.mdau.ushirika.module.payment.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.payment.dto.PaymentLinkDto;
import com.mdau.ushirika.module.payment.service.PaymentLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/payment-links")
@RequiredArgsConstructor
@Tag(name = "Payment Links — Public", description = "Active payment handles shown to members on the pay page")
public class PublicPaymentLinksController {

    private final PaymentLinkService service;

    @GetMapping
    @Operation(summary = "List all active payment links — no authentication required")
    public ResponseEntity<ApiResponse<List<PaymentLinkDto>>> listActive() {
        return ResponseEntity.ok(ApiResponse.ok("Payment links retrieved", service.listActive()));
    }
}
