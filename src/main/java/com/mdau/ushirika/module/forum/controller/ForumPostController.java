package com.mdau.ushirika.module.forum.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.forum.dto.ForumPostDto;
import com.mdau.ushirika.module.forum.dto.SubmitForumPostRequest;
import com.mdau.ushirika.module.forum.service.ForumPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forum/posts")
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Forum — Member", description = "Submit and view your own testimonial stories")
public class ForumPostController {

    private final ForumPostService forumPostService;

    @PostMapping
    @Operation(summary = "Submit a testimonial story (goes to admin review queue)")
    public ResponseEntity<ApiResponse<ForumPostDto>> submit(@Valid @RequestBody SubmitForumPostRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Story submitted for review", forumPostService.submit(req)));
    }

    @GetMapping("/my")
    @Operation(summary = "List my submitted stories and their statuses")
    public ResponseEntity<ApiResponse<List<ForumPostDto>>> myPosts() {
        return ResponseEntity.ok(ApiResponse.ok("Your stories", forumPostService.myPosts()));
    }
}
