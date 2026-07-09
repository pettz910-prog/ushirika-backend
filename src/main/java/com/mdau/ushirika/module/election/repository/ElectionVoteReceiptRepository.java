package com.mdau.ushirika.module.election.repository;

import com.mdau.ushirika.module.election.entity.ElectionVoteReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ElectionVoteReceiptRepository extends JpaRepository<ElectionVoteReceipt, UUID> {

    boolean existsByElectionIdAndSeatIdAndVoterId(UUID electionId, UUID seatId, UUID voterId);

    /** Seat IDs for which the voter has already cast a vote in this election. */
    @Query("SELECT r.seat.id FROM ElectionVoteReceipt r WHERE r.election.id = :electionId AND r.voter.id = :voterId")
    List<UUID> findVotedSeatIdsByElectionAndVoter(UUID electionId, UUID voterId);

    long countByElectionIdAndSeatId(UUID electionId, UUID seatId);

    long countByElectionId(UUID electionId);
}
