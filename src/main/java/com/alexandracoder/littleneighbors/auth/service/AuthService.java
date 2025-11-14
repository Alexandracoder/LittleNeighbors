package com.alexandracoder.littleneighbors.auth.service;

import com.alexandracoder.littleneighbors.auth.dto.AuthRequest;
import com.alexandracoder.littleneighbors.auth.dto.AuthResponse;
import com.alexandracoder.littleneighbors.auth.dto.RefreshRequest;
import com.alexandracoder.littleneighbors.security.JwtService;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest request) {

        UserEntity user = repository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        Map<String, Object> claims = Map.of(
                "roles", user.getRoles().stream().map(Enum::name).collect(Collectors.toList())
        );

        String access = jwtService.generateAccessToken(user.getEmail(), claims);
        String refresh = jwtService.generateRefreshToken(user.getEmail());

        return new AuthResponse(access, refresh);
    }

    public AuthResponse refresh(RefreshRequest request) {
        String email = jwtService.extractEmail(request.refreshToken());

        UserEntity user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> claims = Map.of(
                "roles", user.getRoles().stream().map(Enum::name).collect(Collectors.toList())
        );

        String newAccess = jwtService.generateAccessToken(email, claims);

        return new AuthResponse(newAccess, request.refreshToken());
    }
}
