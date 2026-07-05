package com.alexandracoder.littleneighbors.shared.config;

import com.alexandracoder.littleneighbors.enums.Role;
import com.alexandracoder.littleneighbors.enums.VerificationStatus;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Rota la contraseña del usuario admin en producción a partir de variables
 * de entorno, en vez de depender del hash fijo que quedó versionado en
 * V3__create_admin_user.sql (ese fichero no se toca porque Flyway rechaza
 * migraciones ya aplicadas cuyo checksum cambia).
 *
 * No hace nada si ADMIN_BOOTSTRAP_EMAIL / ADMIN_BOOTSTRAP_PASSWORD no están
 * definidas, así que es seguro dejarlo siempre activo: solo se dispara
 * cuando quieras rotar la contraseña a propósito.
 */
@Component
@Profile("prod")
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_BOOTSTRAP_EMAIL:}")
    private String bootstrapEmail;

    @Value("${ADMIN_BOOTSTRAP_PASSWORD:}")
    private String bootstrapPassword;

    @Override
    public void run(String... args) {
        if (bootstrapEmail == null || bootstrapEmail.isBlank()
                || bootstrapPassword == null || bootstrapPassword.isBlank()) {
            return;
        }

        String encoded = passwordEncoder.encode(bootstrapPassword);

        userRepository.findByEmail(bootstrapEmail).ifPresentOrElse(
                existing -> {
                    existing.setPassword(encoded);
                    userRepository.save(existing);
                },
                () -> {
                    UserEntity admin = UserEntity.builder()
                            .email(bootstrapEmail)
                            .firstName("Admin")
                            .lastName("User")
                            .password(encoded)
                            .roles(new HashSet<>(Set.of(Role.ADMIN)))
                            .verificationStatus(VerificationStatus.VERIFIED)
                            .build();
                    userRepository.save(admin);
                }
        );
    }
}
