package com.mdau.ushirika.module.payment.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.payment.entity.PeerPayment;
import com.mdau.ushirika.module.payment.enums.PeerPaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PeerPaymentRepository extends JpaRepository<PeerPayment, UUID> {

    Page<PeerPayment> findAllByMemberOrderByCreatedAtDesc(User member, Pageable pageable);

    Page<PeerPayment> findAllByStatusOrderByCreatedAtDesc(PeerPaymentStatus status, Pageable pageable);

    Page<PeerPayment> findAllByOrderByCreatedAtDesc(Pageable pageable);

    boolean existsByMemberAndMemberTxReferenceIgnoreCase(User member, String memberTxReference);

    /** Cross-member check — same TX reference must never be claimed by two different members. */
    boolean existsByMemberTxReferenceIgnoreCase(String memberTxReference);
}
