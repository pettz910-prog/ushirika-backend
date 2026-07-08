package com.mdau.ushirika.module.benevolence.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.benevolence.dto.ClaimCategoryDto;
import com.mdau.ushirika.module.benevolence.service.BenevolenceClaimCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/benevolence")
@RequiredArgsConstructor
public class PublicBenevolenceController {

    private final BenevolenceClaimCategoryService categoryService;

    /** Active claim categories — used by portal claim form and public programs page. */
    @GetMapping("/claim-categories")
    public ResponseEntity<ApiResponse<List<ClaimCategoryDto>>> listActiveCategories() {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.listActive()));
    }
}
