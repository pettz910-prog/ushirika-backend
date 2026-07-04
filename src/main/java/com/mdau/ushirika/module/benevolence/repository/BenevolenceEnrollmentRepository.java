package com.mdau.ushirika.module.benevolence.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.benevolence.entity.BenevolenceEnrollment;
import com.mdau.ushirika.module.benevolence.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BenevolenceEnrollmentRepository extends JpaRepository<BenevolenceEnrollment, UUID> {
    Optional<BenevolenceEnrollment> findByUser(User user);
    boolean existsByUser(User user);
    Page<BenevolenceEnrollment> findAllByOrderByEnrolledAtDesc(Pageable pageable);
    Page<BenevolenceEnrollment> findAllByStatusOrderByEnrolledAtDesc(EnrollmentStatus status, Pageable pageable);
    List<BenevolenceEnrollment> findAllByStatus(EnrollmentStatus status);
}
