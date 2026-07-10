package com.mdau.ushirika.module.attendance.repository;

import com.mdau.ushirika.module.attendance.entity.Fine;
import com.mdau.ushirika.module.attendance.enums.FineStatus;
import com.mdau.ushirika.module.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FineRepository extends JpaRepository<Fine, UUID> {

    Page<Fine> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Fine> findByStatusOrderByCreatedAtDesc(FineStatus status, Pageable pageable);

    List<Fine> findByUserOrderByCreatedAtDesc(User user);

    List<Fine> findByUserAndStatusOrderByDueDateAsc(User user, FineStatus status);

    List<Fine> findByStatusAndDueDate(FineStatus status, java.time.LocalDate dueDate);
}
