package com.elearning.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendResetEmail(String toEmail, String token) {
        try {
            log.info("Attempting to send reset email to: {}", toEmail);
            log.info("Using sender email: {}", fromEmail);

            String resetLink = frontendUrl + "/reset-password?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Reset Your Password — E-Learning Platform");
            message.setText(
                    "Hello,\n\n" +
                            "You requested to reset your password.\n\n" +
                            "Click the link below (valid for 15 minutes):\n\n" +
                            resetLink + "\n\n" +
                            "If you did not request this, ignore this email.\n\n" +
                            "E-Learning Team"
            );

            mailSender.send(message);
            log.info("✅ Reset email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("❌ Failed to send email to {}: {}", toEmail, e.getMessage());
            log.error("Full error: ", e);
            // Re-throw so the controller returns an error to Angular
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}