package com.studentmanagement.controller;

import com.studentmanagement.dto.ChangePasswordRequest;
import com.studentmanagement.dto.LoginRequest;
import com.studentmanagement.dto.PasswordResetRequest;
import com.studentmanagement.dto.PasswordSetupRequest;
import com.studentmanagement.dto.RegisterRequest;
import com.studentmanagement.entity.User;
import com.studentmanagement.service.ChangePasswordService;
import com.studentmanagement.service.LoginService;
import com.studentmanagement.service.PasswordResetService;
import com.studentmanagement.service.PasswordSetupService;
import com.studentmanagement.service.RegisterService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    @Autowired
    private PasswordSetupService passwordSetupService;

    @Autowired
    private ChangePasswordService changePasswordService;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/setup-password")
    public ResponseEntity<Map<String, String>> setupPassword(
            @Valid @RequestBody PasswordSetupRequest request) {

        passwordSetupService.setupPassword(request);

        return ResponseEntity.ok(
                Map.of("message",
                        "Password set successfully. You can now log in.")
        );
    }

    @PostMapping("/register")
    public Map<String, Object> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = registerService.register(request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());

        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> login(
            @Valid @RequestBody LoginRequest request) {

        User user = loginService.login(request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());

        return response;
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        changePasswordService.changePassword(request, authentication);

        return ResponseEntity.ok(
                Map.of("message", "Password changed successfully")
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        if (request.getEmail() == null
                || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Email is required"));
        }

        passwordResetService.requestPasswordReset(
                request.getEmail());

        return ResponseEntity.ok(
                Map.of("message",
                        "If an account exists with this email, "
                                + "a password reset link will be sent.")
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody PasswordResetRequest request) {

        passwordResetService.resetPassword(request);

        return ResponseEntity.ok(
                Map.of("message",
                        "Password reset successfully. You can now log in.")
        );
    }

    public static class ForgotPasswordRequest {

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}