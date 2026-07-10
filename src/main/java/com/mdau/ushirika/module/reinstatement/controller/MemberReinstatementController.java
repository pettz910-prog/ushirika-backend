package com.mdau.ushirika.module.reinstatement.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.reinstatement.dto.ReinstatementRequestDto;
import com.mdau.ushirika.module.reinstatement.dto.SubmitReinstatementRequest;
import com.mdau.ushirika.module.reinstatement.service.ReinstatementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberReinstatementController {

    private final ReinstatementService reinstatementService;

    @PostMapping("/reinstatement/request")
    public ApiResponse<ReinstatementRequestDto> submit(@Valid @RequestBody SubmitReinstatementRequest req) {
        return ApiResponse.ok("Reinstatement request submitted.", reinstatementService.submitRequest(req));
    }

    @GetMapping("/reinstatement/my")
    public ApiResponse<List<ReinstatementRequestDto>> myRequests() {
        return ApiResponse.ok(reinstatementService.getMyRequests());
    }
}
