package com.mdau.ushirika.module.mgr.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.mgr.dto.MgrSlotDto;
import com.mdau.ushirika.module.mgr.service.MgrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/mgr")
@RequiredArgsConstructor
public class MemberMgrController {

    private final MgrService mgrService;

    /** Get my slot in the active cycle (or a specific cycle). */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<MgrSlotDto>> getMySlot(
            @RequestParam(required = false) UUID cycleId) {
        return ResponseEntity.ok(ApiResponse.ok(mgrService.getMySlot(cycleId)));
    }
}
