package com.alexandracoder.littleneighbors.auth.controller;

import com.alexandracoder.littleneighbors.auth.dto.*;
import com.alexandracoder.littleneighbors.auth.service.AuthService;
import com.alexandracoder.littleneighbors.email.dto.EmailRequest;
import com.alexandracoder.littleneighbors.profile.dto.UserProfileDTO;
import com.alexandracoder.littleneighbors.shared.exceptions.UnauthorizedAccessException;
import com.alexandracoder.littleneighbors.shared.exceptions.UserAlreadyExistsException;
import com.alexandracoder.littleneighbors.shared.ratelimit.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RateLimiterService rateLimiterService;

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request, HttpServletRequest httpRequest) {
        String ip = resolveClientIp(httpRequest);

        // 10 intentos por IP cada 5 minutos: suficiente para un usuario despistado,
        // demasiado lento para fuerza bruta.
        if (!rateLimiterService.isAllowed("auth-login:ip:" + ip, 10, 300)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Too many login attempts. Please try again later."));
        }

        // Además, por email, para que no se pueda malgastar el cupo de IP
        // probando contraseñas contra una sola cuenta desde varias IPs.
        if (!rateLimiterService.isAllowed("auth-login:email:" + request.email(), 10, 300)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Too many login attempts for this account. Please try again later."));
        }

        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) throws UserAlreadyExistsException {
        String ip = resolveClientIp(httpRequest);

        // 5 registros por IP cada hora: evita scripts creando cuentas en bucle.
        if (!rateLimiterService.isAllowed("auth-register:ip:" + ip, 5, 3600)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Too many registration attempts. Please try again later."));
        }

        authService.register(request);
        authService.sendWelcomeEmail(request.email(), request.firstName());
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        AuthResponse response = authService.reloadUserTokenFromRefresh(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserProfileDTO profile = authService.getCurrentProfile(principal.getName());
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody EmailRequest request, HttpServletRequest httpRequest) {
        String ip = resolveClientIp(httpRequest);

        // 3 solicitudes por IP y por email cada 15 min: evita usar este endpoint
        // para bombardear de emails a una bandeja de entrada ajena.
        if (!rateLimiterService.isAllowed("auth-forgot:ip:" + ip, 3, 900)
                || !rateLimiterService.isAllowed("auth-forgot:email:" + request.email(), 3, 900)) {
            // Misma respuesta que el caso normal a propósito: no revelamos
            // si el email existe ni que se ha activado un límite.
            return ResponseEntity.ok("If the email exists, you will receive a recovery message.");
        }

        authService.initiatePasswordReset(request.email());
        return ResponseEntity.ok("If the email exists, you will receive a recovery message.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request.token(), request.newPassword());
            return ResponseEntity.ok("Password updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
