package com.mdau.ushirika.module.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SubmitForumPostRequest(

        @NotBlank(message = "A title is required")
        @Size(max = 200, message = "Title must be 200 characters or fewer")
        String title,

        @NotBlank(message = "Story content is required")
        @Size(min = 50, message = "Please write at least 50 characters so your story is meaningful")
        String body,

        /** Up to 5 hosted image URLs (Cloudinary, etc.). */
        @Size(max = 5, message = "You can attach up to 5 images")
        List<String> mediaUrls,

        /** Optional YouTube or Vimeo URL. */
        @Size(max = 500)
        String videoUrl
) {}
