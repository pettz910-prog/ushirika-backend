package com.mdau.ushirika.module.election.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "election_results",
    indexes = {
        @Index(name = "idx_eres_election", columnList = "election_id"),
        @Index(name = "idx_eres_seat",     columnList = "seat_id"),
        @Index(name = "idx_eres_winner",   columnList = "is_winner")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectionResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "election_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_eres_election"))
    private Election election;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_eres_seat"))
    private ElectionSeat seat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidacy_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_eres_candidacy"))
    private ElectionCandidacy candidacy;

    /** Denormalized for reporting after users may be deactivated. */
    @Column(name = "member_name", nullable = false, length = 200)
    private String memberName;

    @Column(name = "member_id", length = 20)
    private String memberId;

    @Column(name = "seat_title", nullable = false, length = 150)
    private String seatTitle;

    @Column(name = "vote_count", nullable = false)
    private long voteCount;

    @Column(name = "rank", nullable = false)
    private int rank;

    @Column(name = "is_winner", nullable = false)
    @Builder.Default
    private boolean winner = false;

    /** True if this position was awarded after a tie-break. */
    @Column(name = "tie_broken", nullable = false)
    @Builder.Default
    private boolean tieBroken = false;

    @Column(length = 500)
    private String notes;
}
