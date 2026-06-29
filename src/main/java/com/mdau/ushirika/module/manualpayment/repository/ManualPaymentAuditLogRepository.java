package com.mdau.ushirika.module.manualpayment.repository;

import com.mdau.ushirika.module.manualpayment.entity.ManualPayment;
import com.mdau.ushirika.module.manualpayment.entity.ManualPaymentAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ManualPaymentAuditLogRepository extends JpaRepository<ManualPaymentAuditLog, UUID> {

    List<ManualPaymentAuditLog> findAllByManualPaymentOrderByCreatedAtAsc(ManualPayment manualPayment);
}
