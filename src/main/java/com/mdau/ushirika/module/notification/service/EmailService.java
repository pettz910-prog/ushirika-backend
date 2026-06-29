package com.mdau.ushirika.module.notification.service;

/**
 * Pluggable email abstraction. Implemented by BrevoEmailService.
 * In dev, falls back to logging when API key is not configured.
 */
public interface EmailService {

    void sendEmailVerificationOtp(String toEmail, String name, String otp);

    void sendPasswordResetOtp(String toEmail, String name, String otp);

    void sendWelcome(String toEmail, String name, String memberId);

    void sendPlain(String toEmail, String toName, String subject, String htmlBody);
}
