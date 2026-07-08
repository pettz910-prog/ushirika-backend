package com.mdau.ushirika.module.forum.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.forum.dto.ForumPostDto;
import com.mdau.ushirika.module.forum.service.ForumPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/forum/posts")
@RequiredArgsConstructor
@Tag(name = "Forum — Public", description = "Approved member testimonials shown on the public website")
public class PublicForumPostController {

    private final ForumPostService forumPostService;

    @GetMapping
    @Operation(summary = "Get approved testimonials — featured first, newest next")
    public ResponseEntity<ApiResponse<PagedResponse<ForumPostDto>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "approvedAt"));
        return ResponseEntity.ok(ApiResponse.ok("Testimonials retrieved", forumPostService.publicApproved(pr)));
    }
}
