package com.mdau.ushirika.module.content.dto;

import com.mdau.ushirika.module.content.entity.Article;
import com.mdau.ushirika.module.content.enums.ArticleStatus;
import com.mdau.ushirika.module.content.enums.ArticleType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** Lightweight view for listing pages — omits the full content blocks. */
public record ArticleSummaryDto(
        UUID id,
        String title,
        String slug,
        String excerpt,
        ArticleType type,
        ArticleStatus status,
        String coverImageUrl,
        List<String> tags,
        LocalDateTime publishedAt
) {
    public static ArticleSummaryDto from(Article a) {
        return new ArticleSummaryDto(
                a.getId(), a.getTitle(), a.getSlug(), a.getExcerpt(),
                a.getType(), a.getStatus(), a.getCoverImageUrl(),
                a.getTags(), a.getPublishedAt()
        );
    }
}
