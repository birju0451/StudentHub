package com.studentmanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public PasswordResetEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(
            String recipientEmail,
            String recipientName,
            String token) {

        String resetLink = frontendUrl
                + "/pages/reset-password.html?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject("Reset your StudentHub password");

        message.setText(
                "Hello " + recipientName + ",\n\n"
                        + "We received a request to reset your password.\n\n"
                        + "Use the link below to choose a new password:\n"
                        + resetLink + "\n\n"
                        + "This link expires in 15 minutes and can only be used once.\n\n"
                        + "If you did not request this reset, you can ignore this email."
        );

        mailSender.send(message);
    }
}
