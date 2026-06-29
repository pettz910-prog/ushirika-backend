package com.mdau.ushirika.module.content.dto;

import com.mdau.ushirika.module.content.entity.Article;
import com.mdau.ushirika.module.content.enums.ArticleStatus;
import com.mdau.ushirika.module.content.enums.ArticleType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ArticleDto(
        UUID id,
        String title,
        String slug,
        String excerpt,
        ArticleType type,
        ArticleStatus status,
        String coverImageUrl,
        List<Map<String, Object>> content,
        List<String> tags,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ArticleDto from(Article a) {
        return new ArticleDto(
                a.getId(), a.getTitle(), a.getSlug(), a.getExcerpt(),
                a.getType(), a.getStatus(), a.getCoverImageUrl(),
                a.getContent(), a.getTags(),
                a.getPublishedAt(), a.getCreatedAt(), a.getUpdatedAt()
        );
    }
}
