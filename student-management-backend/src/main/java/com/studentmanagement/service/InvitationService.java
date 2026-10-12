
package com.studentmanagement.service;

import com.studentmanagement.entity.InvitationToken;
import com.studentmanagement.entity.User;
import com.studentmanagement.repository.InvitationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class InvitationService {

    @Autowired
    private InvitationTokenRepository invitationTokenRepository;

    @Autowired
    private InvitationEmailService invitationEmailService;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void createAndSendInvitation(User user) {

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        InvitationToken invitation = new InvitationToken();
        invitation.setTokenHash(hashToken(token));
        invitation.setUser(user);
        invitation.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        invitation.setUsed(false);

        invitationTokenRepository.save(invitation);

        // If sending fails, the exception propagates so the caller
        // can roll back student/account creation in its transaction.
        invitationEmailService.sendPasswordSetupInvitation(
                user.getEmail(),
                user.getName(),
                token
        );
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return java.util.HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "Unable to generate invitation token hash",
                    exception
            );
        }
    }
}
