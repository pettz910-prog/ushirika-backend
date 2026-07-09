package com.mdau.ushirika.module.election.repository;

import com.mdau.ushirika.module.election.entity.Election;
import com.mdau.ushirika.module.election.enums.ElectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ElectionRepository extends JpaRepository<Election, UUID> {

    List<Election> findAllByOrderByYearDescCreatedAtDesc();

    Optional<Election> findFirstByStatusOrderByYearDesc(ElectionStatus status);

    boolean existsByStatusIn(List<ElectionStatus> statuses);
}
