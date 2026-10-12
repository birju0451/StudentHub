package com.studentmanagement.service;

import com.studentmanagement.dto.ChangePasswordRequest;
import com.studentmanagement.entity.User;
import com.studentmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void changePassword(
            ChangePasswordRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {
            throw new org.springframework.security.authentication.BadCredentialsException(
                    "Current password is incorrect"
            );
        }

        if (request.getNewPassword() == null
                || request.getNewPassword().length() < 8) {
            throw new IllegalArgumentException(
                    "New password must contain at least 8 characters");
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {
            throw new IllegalArgumentException(
                    "New password must be different from the current password");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }
}