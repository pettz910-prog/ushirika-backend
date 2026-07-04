package com.mdau.ushirika.module.benevolence.repository;

import com.mdau.ushirika.module.benevolence.entity.BenevolenceEnrollment;
import com.mdau.ushirika.module.benevolence.entity.EnrollmentPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EnrollmentPaymentRepository extends JpaRepository<EnrollmentPayment, UUID> {
    List<EnrollmentPayment> findByEnrollmentOrderByPaidAtDesc(BenevolenceEnrollment enrollment);
}
