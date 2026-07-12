package com.mdau.ushirika.module.dues.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.dues.entity.DuesPayment;
import com.mdau.ushirika.module.dues.entity.MembershipDue;
import com.mdau.ushirika.module.dues.enums.DuesPaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface DuesPaymentRepository extends JpaRepository<DuesPayment, UUID> {

    List<DuesPayment> findAllByDuesOrderByCreatedAtAsc(MembershipDue dues);

    Page<DuesPayment> findAllByMemberOrderByCreatedAtDesc(User member, Pageable pageable);

    Page<DuesPayment> findAllByStatusOrderByCreatedAtDesc(DuesPaymentStatus status, Pageable pageable);

    Page<DuesPayment> findAllByOrderByCreatedAtDesc(Pageable pageable);

    boolean existsByMemberTxReferenceIgnoreCase(String memberTxReference);

    @Query("""
           SELECT COALESCE(SUM(p.amount), 0)
           FROM DuesPayment p
           WHERE p.dues = :dues AND p.status = 'VERIFIED'
           """)
    BigDecimal sumVerifiedAmount(@Param("dues") MembershipDue dues);
}
