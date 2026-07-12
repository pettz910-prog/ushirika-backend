package com.mdau.ushirika.module.attendance.repository;

import com.mdau.ushirika.module.attendance.entity.Fine;
import com.mdau.ushirika.module.attendance.entity.FinePayment;
import com.mdau.ushirika.module.attendance.enums.FinePaymentStatus;
import com.mdau.ushirika.module.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinePaymentRepository extends JpaRepository<FinePayment, UUID> {

    Optional<FinePayment> findByFineAndStatus(Fine fine, FinePaymentStatus status);

    boolean existsByMemberTxReferenceIgnoreCase(String memberTxReference);

    List<FinePayment> findAllByMemberOrderByCreatedAtDesc(User member);

    Page<FinePayment> findAllByStatusOrderByCreatedAtDesc(FinePaymentStatus status, Pageable pageable);

    Page<FinePayment> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
