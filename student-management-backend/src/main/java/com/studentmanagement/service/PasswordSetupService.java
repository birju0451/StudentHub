
package com.studentmanagement.service;

import com.studentmanagement.dto.PasswordSetupRequest;
import com.studentmanagement.entity.InvitationToken;
import com.studentmanagement.entity.User;
import com.studentmanagement.repository.InvitationTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class PasswordSetupService {

    @Autowired
    private InvitationTokenRepository invitationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void setupPassword(PasswordSetupRequest request) {

        String tokenHash = hashToken(request.getToken());

        InvitationToken invitation = invitationTokenRepository
                .findByTokenHashAndUsedFalse(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid or already used invitation link"
                ));

        if (!invitation.getExpiresAt().isAfter(LocalDateTime.now())) {
            invitationTokenRepository.delete(invitation);

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invitation link has expired. Contact your administrator."
            );
        }

        User user = invitation.getUser();

        if (user.isPasswordSetupComplete()) {
            invitation.setUsed(true);
            invitationTokenRepository.save(invitation);

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password has already been set"
            );
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordSetupComplete(true);

        invitation.setUsed(true);

        // The transaction saves both updated entities.
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "Unable to process invitation token", exception
            );
        }
    }
}
