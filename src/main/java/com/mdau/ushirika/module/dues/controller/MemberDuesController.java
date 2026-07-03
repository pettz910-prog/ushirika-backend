package com.mdau.ushirika.module.dues.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.dues.dto.MembershipDueDto;
import com.mdau.ushirika.module.dues.service.MembershipDuesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberDuesController {

    private final MembershipDuesService duesService;

    @GetMapping("/dues/my")
    public ResponseEntity<ApiResponse<List<MembershipDueDto>>> getMyDues() {
        return ResponseEntity.ok(ApiResponse.ok("Dues fetched", duesService.getMyDues()));
    }
}
