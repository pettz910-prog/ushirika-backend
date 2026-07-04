package com.mdau.ushirika.module.loan.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.loan.dto.*;
import com.mdau.ushirika.module.loan.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class MemberLoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<ApiResponse<LoanApplicationDto>> applyForLoan(
            @Valid @RequestBody ApplyForLoanRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Loan application submitted",
                loanService.applyForLoan(req)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<LoanApplicationDto>>> getMyLoans() {
        return ResponseEntity.ok(ApiResponse.ok(loanService.getMyLoans()));
    }

    @GetMapping("/my/{id}")
    public ResponseEntity<ApiResponse<LoanApplicationDto>> getMyLoan(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(loanService.getMyLoanById(id)));
    }

    @GetMapping("/my/guarantor-requests")
    public ResponseEntity<ApiResponse<List<LoanGuarantorDto>>> getMyGuarantorRequests() {
        return ResponseEntity.ok(ApiResponse.ok(loanService.getMyGuarantorRequests()));
    }

    @PatchMapping("/guarantors/{guarantorId}")
    public ResponseEntity<ApiResponse<LoanGuarantorDto>> respondToGuarantor(
            @PathVariable UUID guarantorId,
            @Valid @RequestBody GuarantorResponseRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Response recorded",
                loanService.respondToGuarantorRequest(guarantorId, req)));
    }
}
