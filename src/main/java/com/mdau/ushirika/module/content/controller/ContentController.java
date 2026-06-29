package com.mdau.ushirika.module.content.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.content.dto.ArticleDto;
import com.mdau.ushirika.module.content.dto.ArticleSummaryDto;
import com.mdau.ushirika.module.content.enums.ArticleType;
import com.mdau.ushirika.module.content.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/content")
@RequiredArgsConstructor
@Tag(name = "Content — Public", description = "Browse published articles and news")
public class ContentController {

    private final ArticleService articleService;

    @GetMapping("/articles")
    @Operation(summary = "List published articles, optionally filtered by type")
    public ResponseEntity<ApiResponse<PagedResponse<ArticleSummaryDto>>> list(
            @RequestParam(required = false) ArticleType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Articles retrieved",
                articleService.listPublished(type,
                        PageRequest.of(page, size, Sort.by("publishedAt").descending()))));
    }

    @GetMapping("/articles/{slug}")
    @Operation(summary = "Get a single published article by its slug")
    public ResponseEntity<ApiResponse<ArticleDto>> get(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.ok("Article retrieved",
                articleService.getPublishedBySlug(slug)));
    }
}
