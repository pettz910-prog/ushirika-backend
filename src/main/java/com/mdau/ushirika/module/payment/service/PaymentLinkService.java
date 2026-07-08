package com.mdau.ushirika.module.payment.service;

import com.mdau.ushirika.module.payment.dto.PaymentLinkDto;
import com.mdau.ushirika.module.payment.dto.UpsertPaymentLinkRequest;
import com.mdau.ushirika.module.payment.entity.PaymentLink;
import com.mdau.ushirika.module.payment.enums.PaymentChannel;
import com.mdau.ushirika.module.payment.repository.PaymentLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentLinkService {

    private final PaymentLinkRepository repo;

    @Transactional(readOnly = true)
    public List<PaymentLinkDto> listAll() {
        return repo.findAllByOrderByDisplayOrderAscCreatedAtAsc()
                   .stream().map(PaymentLinkDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentLinkDto> listActive() {
        return repo.findAllByActiveTrueOrderByDisplayOrderAscCreatedAtAsc()
                   .stream().map(PaymentLinkDto::from).toList();
    }

    /** Creates or updates the payment link for a given channel. One record per channel. */
    @Transactional
    public PaymentLinkDto upsert(PaymentChannel channel, UpsertPaymentLinkRequest req) {
        PaymentLink link = repo.findByChannel(channel)
                .orElseGet(() -> PaymentLink.builder().channel(channel).build());

        link.setHandle(req.handle().strip());
        link.setDisplayName(req.displayName());
        link.setInstructions(req.instructions());
        link.setDeepLinkUrl(req.deepLinkUrl());
        link.setActive(req.active());
        link.setDisplayOrder(req.displayOrder());

        return PaymentLinkDto.from(repo.save(link));
    }

    @Transactional
    public void delete(PaymentChannel channel) {
        repo.findByChannel(channel).ifPresent(repo::delete);
    }
}
