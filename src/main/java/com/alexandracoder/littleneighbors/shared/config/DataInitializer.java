package com.alexandracoder.littleneighbors.shared.config;

import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import com.alexandracoder.littleneighbors.enums.Role;
import com.alexandracoder.littleneighbors.enums.VerificationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@littleneighbors.com").isEmpty()) {
            UserEntity admin = UserEntity.builder()
                    .email("admin@littleneighbors.com")
                    .firstName("Admin")
                    .lastName("System")
                    .password(passwordEncoder.encode("admin1234"))
                    .roles(new HashSet<>(Set.of(Role.ADMIN)))
                    .verificationStatus(VerificationStatus.VERIFIED)
                    .build();
            userRepository.save(admin);
        }

        if (userRepository.findByEmail("test@littleneighbors.com").isEmpty()) {
            UserEntity testUser = UserEntity.builder()
                    .email("test@littleneighbors.com")
                    .firstName("Usuario")
                    .lastName("Prueba")
                    .password(passwordEncoder.encode("test1234"))
                    .roles(new HashSet<>(Set.of(Role.USER)))
                    .verificationStatus(VerificationStatus.PENDING_REVIEW)
                    .build();
            userRepository.save(testUser);
        }
    }
}