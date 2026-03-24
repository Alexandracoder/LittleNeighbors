package com.alexandracoder.littleneighbors.auth.controller;

import com.alexandracoder.littleneighbors.auth.dto.*;
import com.alexandracoder.littleneighbors.auth.service.AuthService;
import com.alexandracoder.littleneighbors.profile.dto.UserProfileDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        // El service ahora devuelve solo los tokens (access y refresh)
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        // Asegúrate de que RefreshRequest sea un record: record RefreshRequest(String refreshToken) {}
        AuthResponse response = authService.reloadUserTokenFromRefresh(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    /**
     * Este es el endpoint que tu AuthContext de React llama
     * justo después del login para poblar la familia y los hijos.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // principal.getName() nos da el email extraído del JWT por Spring Security
        UserProfileDTO profile = authService.getCurrentProfile(principal.getName());
        return ResponseEntity.ok(profile);
    }
}


