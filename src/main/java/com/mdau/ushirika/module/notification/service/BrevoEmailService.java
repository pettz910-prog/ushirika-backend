package com.mdau.ushirika.module.notification.service;

import com.mdau.ushirika.module.notification.entity.NotificationLog;
import com.mdau.ushirika.module.notification.enums.NotificationChannel;
import com.mdau.ushirika.module.notification.enums.NotificationStatus;
import com.mdau.ushirika.module.notification.repository.NotificationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BrevoEmailService implements EmailService {

    private final WebClient.Builder webClientBuilder;
    private final NotificationLogRepository logRepository;

    @Value("${app.brevo.api-key:NOT_SET}")
    private String apiKey;

    @Value("${app.brevo.sender-email}")
    private String senderEmail;

    @Value("${app.brevo.sender-name}")
    private String senderName;

    public BrevoEmailService(WebClient.Builder webClientBuilder,
                             NotificationLogRepository logRepository) {
        this.webClientBuilder = webClientBuilder;
        this.logRepository    = logRepository;
    }

    @Async
    @Override
    public void sendEmailVerificationOtp(String toEmail, String name, String otp) {
        String subject = "Your Ushirika Welfare Verification Code";
        String html = """
                <div style="font-family:sans-serif;max-width:480px;margin:auto">
                  <h2 style="color:#1A4731">Verify Your Email</h2>
                  <p>Hi %s,</p>
                  <p>Use the code below to verify your Ushirika Welfare account. It expires in <strong>15 minutes</strong>.</p>
                  <div style="font-size:36px;font-weight:700;letter-spacing:8px;color:#1A4731;margin:24px 0">%s</div>
                  <p style="color:#888;font-size:13px">If you did not register, ignore this email.</p>
                </div>
                """.formatted(name, otp);
        sendPlain(toEmail, name, subject, html);
    }

    @Async
    @Override
    public void sendPasswordResetOtp(String toEmail, String name, String otp) {
        String subject = "Reset Your Ushirika Welfare Password";
        String html = """
                <div style="font-family:sans-serif;max-width:480px;margin:auto">
                  <h2 style="color:#1A4731">Password Reset</h2>
                  <p>Hi %s,</p>
                  <p>Use this code to reset your password. It expires in <strong>15 minutes</strong>.</p>
                  <div style="font-size:36px;font-weight:700;letter-spacing:8px;color:#1A4731;margin:24px 0">%s</div>
                  <p style="color:#888;font-size:13px">If you did not request this, ignore this email.</p>
                </div>
                """.formatted(name, otp);
        sendPlain(toEmail, name, subject, html);
    }

    @Async
    @Override
    public void sendWelcome(String toEmail, String name, String memberId) {
        String subject = "Welcome to Ushirika Welfare!";
        String html = """
                <div style="font-family:sans-serif;max-width:480px;margin:auto">
                  <h2 style="color:#1A4731">Welcome, %s!</h2>
                  <p>Your membership has been approved. Your Member ID is:</p>
                  <div style="font-size:24px;font-weight:700;color:#1A4731;margin:16px 0">%s</div>
                  <p>Log in to your member portal to view your dashboard, make contributions, and apply for welfare.</p>
                  <p>— Ushirika Welfare Team</p>
                </div>
                """.formatted(name, memberId);
        sendPlain(toEmail, name, subject, html);
    }

    @Async
    @Override
    public void sendPlain(String toEmail, String toName, String subject, String htmlBody) {
        NotificationLog logEntry = logRepository.save(
                NotificationLog.builder()
                        .channel(NotificationChannel.EMAIL)
                        .recipient(toEmail)
                        .recipientName(toName)
                        .subject(subject)
                        .body(truncate(htmlBody, 2000))
                        .status(NotificationStatus.PENDING)
                        .build()
        );

        if ("NOT_SET".equals(apiKey)) {
            log.warn("[DEV EMAIL — not sent] To: {} | Subject: {} | Body: {}", toEmail, subject, htmlBody);
            logEntry.setStatus(NotificationStatus.SENT);
            logRepository.save(logEntry);
            return;
        }

        try {
            Map<String, Object> payload = Map.of(
                    "sender", Map.of("name", senderName, "email", senderEmail),
                    "to", List.of(Map.of("email", toEmail, "name", toName)),
                    "subject", subject,
                    "htmlContent", htmlBody
            );
            webClientBuilder.build()
                    .post()
                    .uri("https://api.brevo.com/v3/smtp/email")
                    .header("api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .subscribe(
                            r -> {
                                log.info("Email sent to {}", toEmail);
                                logEntry.setStatus(NotificationStatus.SENT);
                                logRepository.save(logEntry);
                            },
                            e -> {
                                log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
                                logEntry.setStatus(NotificationStatus.FAILED);
                                logEntry.setErrorMessage(truncate(e.getMessage(), 500));
                                logRepository.save(logEntry);
                            }
                    );
        } catch (Exception e) {
            log.error("Email error for {}: {}", toEmail, e.getMessage());
            logEntry.setStatus(NotificationStatus.FAILED);
            logEntry.setErrorMessage(truncate(e.getMessage(), 500));
            logRepository.save(logEntry);
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }
}
