package com.thy.transport.config;

import com.thy.transport.model.User;
import com.thy.transport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Only add if no users exist
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole("ADMIN");
            userRepository.save(adminUser);
        }

        if (userRepository.findByUsername("burak").isEmpty()) {
            User regularUser = new User();
            regularUser.setUsername("burak");
            regularUser.setPassword(passwordEncoder.encode("yazici"));
            regularUser.setRole("USER");
            userRepository.save(regularUser);
        }

        if (userRepository.findByUsername("test").isEmpty()) {
            User regularUser = new User();
            regularUser.setUsername("test");
            regularUser.setPassword(passwordEncoder.encode("test"));
            regularUser.setRole("TEST");
            userRepository.save(regularUser);
        }
    }
} 