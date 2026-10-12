
package com.studentmanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class InvitationEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public InvitationEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordSetupInvitation(
            String recipientEmail,
            String recipientName,
            String token) {

        String setupLink = frontendUrl
                + "/pages/set-password.html?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject("Set up your StudentHub password");
        message.setText(
                "Hello " + recipientName + ",\n\n"
                        + "Your StudentHub student account has been created.\n\n"
                        + "Use the following link to set your password:\n"
                        + setupLink + "\n\n"
                        + "This link expires in 30 minutes and can only be used once.\n\n"
                        + "If you were not expecting this invitation, please contact your administrator."
        );

        mailSender.send(message);
    }
}
