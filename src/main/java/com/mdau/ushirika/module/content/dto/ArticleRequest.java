package com.mdau.ushirika.module.content.dto;

import com.mdau.ushirika.module.content.enums.ArticleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

public record ArticleRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 300, message = "Title must not exceed 300 characters")
        String title,

        /** Optional — auto-generated from title if not provided. */
        String slug,

        @Size(max = 500, message = "Excerpt must not exceed 500 characters")
        String excerpt,

        @NotNull(message = "Article type is required")
        ArticleType type,

        String coverImageUrl,

        /** Cloudinary public_id for the cover — needed to delete old cover on update. */
        String coverImagePublicId,

        /**
         * Array of content block objects.
         * Each block must have a "type" key at minimum.
         * Backend stores as-is; frontend owns the schema.
         */
        List<Map<String, Object>> content,

        List<String> tags
) {}
