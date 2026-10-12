
package com.studentmanagement.Config;

import com.studentmanagement.entity.Role;
import com.studentmanagement.entity.User;
import com.studentmanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminDataInitializer implements CommandLineRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(AdminDataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.name}")
    private String adminName;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {

        if (!StringUtils.hasText(adminName)
                || !StringUtils.hasText(adminEmail)
                || !StringUtils.hasText(adminPassword)) {
            throw new IllegalStateException(
                    "Admin name, email, and password must be configured"
            );
        }

        if (userRepository.existsByEmail(adminEmail)) {
            logger.info("ADMIN account already exists.");
            return;
        }

        User admin = new User();
        admin.setName(adminName);
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);

        logger.info("First ADMIN account created successfully.");
    }
}
