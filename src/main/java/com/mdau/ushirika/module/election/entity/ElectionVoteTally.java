package com.mdau.ushirika.module.election.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Running vote count per candidacy — contains NO voter reference.
 * Paired with ElectionVoteReceipt to implement a secret ballot.
 * vote_count is incremented via bulk UPDATE (not OL) for concurrency safety.
 */
@Entity
@Table(
    name = "election_vote_tallies",
    indexes = {
        @Index(name = "idx_evt_candidacy", columnList = "candidacy_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectionVoteTally {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidacy_id", nullable = false, unique = true,
                foreignKey = @ForeignKey(name = "fk_evt_candidacy"))
    private ElectionCandidacy candidacy;

    @Column(name = "vote_count", nullable = false)
    @Builder.Default
    private long voteCount = 0L;
}
