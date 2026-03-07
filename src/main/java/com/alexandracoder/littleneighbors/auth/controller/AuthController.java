package com.alexandracoder.littleneighbors.auth.controller;

import com.alexandracoder.littleneighbors.auth.dto.AuthRequest;
import com.alexandracoder.littleneighbors.auth.dto.AuthResponse;
import com.alexandracoder.littleneighbors.auth.dto.RefreshRequest;
import com.alexandracoder.littleneighbors.auth.dto.RegisterRequest;
import com.alexandracoder.littleneighbors.auth.service.AuthService;
import com.alexandracoder.littleneighbors.family.dto.FamilyAuthResponseDTO;
import com.alexandracoder.littleneighbors.user.dto.UserProfileDTO;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<FamilyAuthResponseDTO> login(@RequestBody AuthRequest request) {
        FamilyAuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        AuthResponse response = authService.reloadUserTokenFromRefresh(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(Principal principal) {
        UserProfileDTO profile = authService.getCurrentProfile(principal.getName());
        return ResponseEntity.ok(profile);
    }
}


