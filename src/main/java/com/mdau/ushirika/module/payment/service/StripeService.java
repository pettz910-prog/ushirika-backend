package com.mdau.ushirika.module.payment.service;

import com.mdau.ushirika.common.exception.BadRequestException;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class StripeService {

    private final String secretKey;
    private final String webhookSecret;
    private final boolean devMode;

    public StripeService(
            @Value("${app.stripe.secret-key:NOT_SET}") String secretKey,
            @Value("${app.stripe.webhook-secret:NOT_SET}") String webhookSecret
    ) {
        this.secretKey = secretKey;
        this.webhookSecret = webhookSecret;
        this.devMode = "NOT_SET".equals(secretKey);
    }

    @PostConstruct
    void init() {
        if (!devMode) {
            Stripe.apiKey = secretKey;
        }
    }

    /**
     * Creates a Stripe Checkout Session for a one-time USD payment.
     * Returns the session ID and hosted checkout URL.
     * Amount is in USD — converted to cents (× 100) for Stripe.
     */
    public StripeCheckoutResult createCheckoutSession(
            String email,
            BigDecimal amountUsd,
            String productName,
            String successUrl,
            String cancelUrl,
            Map<String, String> metadata
    ) {
        if (devMode) {
            String fakeSessionId = "cs_dev_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
            log.warn("[Stripe DEV] Simulating checkout session for email={} amount={} product={}",
                    email, amountUsd, productName);
            return new StripeCheckoutResult(fakeSessionId,
                    "https://checkout.stripe.com/dev/" + fakeSessionId);
        }

        long amountCents = amountUsd.multiply(BigDecimal.valueOf(100)).longValue();

        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setCustomerEmail(email)
                .setSuccessUrl(successUrl + (successUrl.contains("?") ? "&" : "?") + "session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(amountCents)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(productName)
                                        .build())
                                .build())
                        .build());

        if (metadata != null) {
            metadata.forEach(builder::putMetadata);
        }

        try {
            Session session = Session.create(builder.build());
            log.info("[Stripe] Checkout session created: id={} amount={} USD email={}",
                    session.getId(), amountUsd, email);
            return new StripeCheckoutResult(session.getId(), session.getUrl());
        } catch (StripeException e) {
            log.error("[Stripe] Failed to create checkout session: {}", e.getMessage());
            throw new BadRequestException("Payment initialization failed: " + e.getMessage());
        }
    }

    /**
     * Verifies the Stripe-Signature header and constructs the typed Event.
     * Throws SignatureVerificationException if the signature is invalid.
     */
    /**
     * Verifies the Stripe-Signature header and constructs the typed Event.
     * Returns null in dev mode (no real Stripe credentials — webhooks are untestable locally).
     * Throws SignatureVerificationException if the signature does not match in prod.
     */
    public Event constructWebhookEvent(String rawBody, String stripeSignature) throws SignatureVerificationException {
        if (devMode) {
            log.warn("[Stripe DEV] No webhook secret configured — ignoring inbound webhook");
            return null;
        }
        return Webhook.constructEvent(rawBody, stripeSignature, webhookSecret);
    }

    public record StripeCheckoutResult(String sessionId, String checkoutUrl) {}
}
