package com.mdau.ushirika.module.election.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.election.enums.ElectionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "elections",
    indexes = {
        @Index(name = "idx_elec_status", columnList = "status"),
        @Index(name = "idx_elec_year",   columnList = "year")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Election extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "nominations_start")
    private LocalDate nominationsStart;

    @Column(name = "nominations_end")
    private LocalDate nominationsEnd;

    @Column(name = "voting_start")
    private LocalDateTime votingStart;

    @Column(name = "voting_end")
    private LocalDateTime votingEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private ElectionStatus status = ElectionStatus.DRAFT;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "cloudinary_video_public_id", length = 300)
    private String cloudinaryVideoPublicId;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "results_declared_at")
    private LocalDateTime resultsDeclaredAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC, title ASC")
    @Builder.Default
    private List<ElectionSeat> seats = new ArrayList<>();
}
