package com.mdau.ushirika.module.manualpayment.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.manualpayment.entity.ManualPayment;
import com.mdau.ushirika.module.manualpayment.enums.ManualPaymentCategory;
import com.mdau.ushirika.module.manualpayment.enums.ManualPaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.UUID;

public interface ManualPaymentRepository extends JpaRepository<ManualPayment, UUID> {

    boolean existsByReceiptNumber(String receiptNumber);

    Page<ManualPayment> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<ManualPayment> findAllByStatusOrderByCreatedAtDesc(ManualPaymentStatus status, Pageable pageable);

    Page<ManualPayment> findAllByCategoryOrderByCreatedAtDesc(ManualPaymentCategory category, Pageable pageable);

    Page<ManualPayment> findAllByRecordedByOrderByCreatedAtDesc(User recordedBy, Pageable pageable);

    Page<ManualPayment> findAllByMemberOrderByCreatedAtDesc(User member, Pageable pageable);

    @Query("SELECT COALESCE(SUM(m.amount), 0) FROM ManualPayment m WHERE m.status = 'APPROVED'")
    BigDecimal sumAllApproved();

    @Query("SELECT COALESCE(SUM(m.amount), 0) FROM ManualPayment m WHERE m.status = 'APPROVED' AND m.category = :category")
    BigDecimal sumApprovedByCategory(ManualPaymentCategory category);

    long countByStatus(ManualPaymentStatus status);
}
