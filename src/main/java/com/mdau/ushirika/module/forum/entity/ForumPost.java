package com.mdau.ushirika.module.forum.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.forum.enums.ForumPostStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A member-submitted testimonial story about how Ushirika has helped them.
 * Requires admin approval before appearing on the public website.
 */
@Entity
@Table(
    name = "forum_posts",
    indexes = {
        @Index(name = "idx_fp_status",      columnList = "status"),
        @Index(name = "idx_fp_member_id",   columnList = "member_id"),
        @Index(name = "idx_fp_approved_at", columnList = "approved_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForumPost extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_fp_member"))
    private User member;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    /** Cloudinary / hosted image URLs (max 5). */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "forum_post_media",
        joinColumns = @JoinColumn(name = "post_id",
                                  foreignKey = @ForeignKey(name = "fk_fpm_post"))
    )
    @Column(name = "url", length = 500)
    @OrderColumn(name = "sort_order")
    @Builder.Default
    private List<String> mediaUrls = new ArrayList<>();

    /** Optional YouTube or Vimeo embed URL. */
    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private ForumPostStatus status = ForumPostStatus.PENDING;

    /** Admin notes — rejection reason or editorial feedback. */
    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id",
                foreignKey = @ForeignKey(name = "fk_fp_reviewed_by"))
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /** Admin can pin a post to the top of the public page. */
    @Column(nullable = false)
    @Builder.Default
    private boolean featured = false;
}
