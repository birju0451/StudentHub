package com.studentmanagement.Config;

import com.studentmanagement.entity.Role;
import com.studentmanagement.entity.User;
import com.studentmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "app.admin.reset-password-on-startup",
        havingValue = "true"
)
public class AdminPasswordResetRunner implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${APP_ADMIN_PASSWORD}")
    private String newAdminPassword;

    @Override
    public void run(String... args) {

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Configured ADMIN account was not found."
                        ));

        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalStateException(
                    "Configured account is not an ADMIN."
            );
        }

        admin.setPassword(
                passwordEncoder.encode(newAdminPassword)
        );

        userRepository.save(admin);

        System.out.println("ADMIN password reset successfully.");
    }
}