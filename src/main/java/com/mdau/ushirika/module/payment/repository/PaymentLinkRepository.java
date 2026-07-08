package com.mdau.ushirika.module.payment.repository;

import com.mdau.ushirika.module.payment.entity.PaymentLink;
import com.mdau.ushirika.module.payment.enums.PaymentChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentLinkRepository extends JpaRepository<PaymentLink, UUID> {

    Optional<PaymentLink> findByChannel(PaymentChannel channel);

    List<PaymentLink> findAllByOrderByDisplayOrderAscCreatedAtAsc();

    List<PaymentLink> findAllByActiveTrueOrderByDisplayOrderAscCreatedAtAsc();
}
