package com.mdau.ushirika.module.leadership.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.leadership.dto.PublicLeadershipDto;
import com.mdau.ushirika.module.leadership.service.LeadershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/leadership")
@RequiredArgsConstructor
public class PublicLeadershipController {

    private final LeadershipService leadershipService;

    @GetMapping
    public ResponseEntity<ApiResponse<PublicLeadershipDto>> getLeadership() {
        return ResponseEntity.ok(ApiResponse.ok(leadershipService.getPublic()));
    }
}
