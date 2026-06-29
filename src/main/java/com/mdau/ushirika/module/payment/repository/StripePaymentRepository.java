package com.mdau.ushirika.module.payment.repository;

import com.mdau.ushirika.module.payment.entity.StripePayment;
import com.mdau.ushirika.module.payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StripePaymentRepository extends JpaRepository<StripePayment, UUID> {

    Optional<StripePayment> findBySessionId(String sessionId);

    boolean existsBySessionId(String sessionId);

    boolean existsBySessionIdAndStatus(String sessionId, PaymentStatus status);
}
