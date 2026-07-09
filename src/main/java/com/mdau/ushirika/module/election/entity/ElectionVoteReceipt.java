package com.mdau.ushirika.module.election.entity;

import com.mdau.ushirika.module.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Proves that a member voted for a seat — contains NO reference to which candidate.
 * Paired with ElectionVoteTally to implement a secret ballot:
 *   receipt proves participation, tally proves outcome — no record links them.
 */
@Entity
@Table(
    name = "election_vote_receipts",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_receipt_election_seat_voter",
        columnNames = {"election_id", "seat_id", "voter_id"}
    ),
    indexes = {
        @Index(name = "idx_evr_election", columnList = "election_id"),
        @Index(name = "idx_evr_seat",     columnList = "seat_id"),
        @Index(name = "idx_evr_voter",    columnList = "voter_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectionVoteReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "election_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_evr_election"))
    private Election election;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_evr_seat"))
    private ElectionSeat seat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voter_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_evr_voter"))
    private User voter;

    @Column(name = "voted_at", nullable = false)
    @Builder.Default
    private LocalDateTime votedAt = LocalDateTime.now();
}
