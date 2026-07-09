package com.mdau.ushirika.module.election.repository;

import com.mdau.ushirika.module.election.entity.ElectionSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ElectionSeatRepository extends JpaRepository<ElectionSeat, UUID> {

    List<ElectionSeat> findAllByElectionIdOrderBySortOrderAscTitleAsc(UUID electionId);
}
