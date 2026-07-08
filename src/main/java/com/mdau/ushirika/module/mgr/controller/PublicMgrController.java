package com.mdau.ushirika.module.mgr.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.mgr.dto.MgrCycleDto;
import com.mdau.ushirika.module.mgr.service.MgrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/public/mgr")
@RequiredArgsConstructor
public class PublicMgrController {

    private final MgrService mgrService;

    /**
     * Returns summary info about the currently active MGR cycle for the public website.
     * Excludes sensitive slot/member data — only cycle-level stats.
     */
    @GetMapping("/active-cycle")
    public ResponseEntity<ApiResponse<MgrCycleDto>> getActiveCycle() {
        Optional<MgrCycleDto> dto = mgrService.getActiveCyclePublicInfo();
        return dto.map(d -> ResponseEntity.ok(ApiResponse.ok(d)))
                .orElseGet(() -> ResponseEntity.ok(ApiResponse.<MgrCycleDto>ok(null)));
    }
}
