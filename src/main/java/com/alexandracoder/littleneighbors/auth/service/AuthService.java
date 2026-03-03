package com.alexandracoder.littleneighbors.auth.service;

import com.alexandracoder.littleneighbors.auth.dto.AuthRequest;
import com.alexandracoder.littleneighbors.auth.dto.AuthResponse;
import com.alexandracoder.littleneighbors.auth.dto.RefreshRequest;
import com.alexandracoder.littleneighbors.auth.dto.RegisterRequest;
import com.alexandracoder.littleneighbors.security.JwtService;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.enums.Role;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante

import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    @Transactional
    public void register(RegisterRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already in use");
        }

        UserEntity user = new UserEntity();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));


        Set<Role> defaultRoles = new HashSet<>();
        defaultRoles.add(Role.USER);
        user.setRoles(defaultRoles);

        repository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        UserEntity user = repository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("DEBUG: Email no encontrado"));

        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("DEBUG: Password incorrecta");
        }

        try {

            System.out.println("DEBUG: Roles encontrados en BD para " + user.getEmail() + ": " + user.getRoles());


            List<String> rolesList = user.getRoles().stream()
                    .map(Enum::name)
                    .collect(Collectors.toList());

            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", rolesList);

            String access = jwtService.generateAccessToken(user.getEmail(), claims);
            String refresh = jwtService.generateRefreshToken(user.getEmail());

            return new AuthResponse(access, refresh);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la generación del token: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(RefreshRequest request) {
        String email = jwtService.extractEmail(request.refreshToken());

        UserEntity user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream().map(Enum::name).collect(Collectors.toList()));

        String newAccess = jwtService.generateAccessToken(email, claims);

        return new AuthResponse(newAccess, request.refreshToken());
    }

    public AuthResponse reloadUserToken(String email) {
        UserEntity user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> rolesList = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", rolesList);

        String access = jwtService.generateAccessToken(user.getEmail(), claims);
        String refresh = jwtService.generateRefreshToken(user.getEmail());

        return new AuthResponse(access, refresh);
    }
    public AuthResponse reloadUserTokenFromRefresh(String refreshToken) {

        String email = jwtService.extractEmail(refreshToken);

        UserEntity user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> rolesList = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", rolesList);

        String newAccessToken = jwtService.generateAccessToken(user.getEmail(), claims);
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}
