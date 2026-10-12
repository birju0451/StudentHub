package com.studentmanagement.service;

import com.studentmanagement.dto.PasswordResetRequest;
import com.studentmanagement.entity.PasswordResetToken;
import com.studentmanagement.entity.User;
import com.studentmanagement.repository.PasswordResetTokenRepository;
import com.studentmanagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordResetEmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void requestPasswordReset(String email) {

        userRepository.findByEmail(email.trim())
                .ifPresent(user -> {
                    byte[] bytes = new byte[32];
                    secureRandom.nextBytes(bytes);

                    String token = Base64.getUrlEncoder()
                            .withoutPadding()
                            .encodeToString(bytes);

                    PasswordResetToken resetToken =
                            new PasswordResetToken();

                    resetToken.setTokenHash(hashToken(token));
                    resetToken.setUser(user);
                    resetToken.setExpiresAt(
                            LocalDateTime.now().plusMinutes(15));
                    resetToken.setUsed(false);

                    tokenRepository.save(resetToken);

                    emailService.sendPasswordResetEmail(
                            user.getEmail(),
                            user.getName(),
                            token);
                });
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {

        String tokenHash = hashToken(request.getToken());

        PasswordResetToken resetToken =
                tokenRepository.findByTokenHashAndUsedFalse(tokenHash)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Invalid or already used reset link"));

        if (!resetToken.getExpiresAt().isAfter(LocalDateTime.now())) {
            resetToken.setUsed(true);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reset link has expired. Request a new one.");
        }

        User user = resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));

        resetToken.setUsed(true);

        userRepository.save(user);
        tokenRepository.save(resetToken);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8));

            return java.util.HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "Unable to process reset token", exception);
        }
    }
}