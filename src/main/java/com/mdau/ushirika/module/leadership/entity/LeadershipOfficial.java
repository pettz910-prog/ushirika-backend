package com.mdau.ushirika.module.leadership.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.leadership.enums.LeadershipTeam;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "leadership_officials",
    indexes = {
        @Index(name = "idx_lo_team",       columnList = "team"),
        @Index(name = "idx_lo_active",     columnList = "active"),
        @Index(name = "idx_lo_sort_order", columnList = "sort_order")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadershipOfficial extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 150)
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LeadershipTeam team;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "cloudinary_public_id", length = 300)
    private String cloudinaryPublicId;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;
}
