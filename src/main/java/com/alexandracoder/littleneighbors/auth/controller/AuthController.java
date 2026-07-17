package com.alexandracoder.littleneighbors.auth.controller;

import com.alexandracoder.littleneighbors.auth.dto.*;
import com.alexandracoder.littleneighbors.auth.service.AuthService;
import com.alexandracoder.littleneighbors.email.dto.EmailRequest;
import com.alexandracoder.littleneighbors.user.dto.UserProfileDTO;
import com.alexandracoder.littleneighbors.shared.exceptions.UnauthorizedAccessException;
import com.alexandracoder.littleneighbors.shared.exceptions.UserAlreadyExistsException;
import com.alexandracoder.littleneighbors.shared.ratelimit.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
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


        if (!rateLimiterService.isAllowed("auth-login:ip:" + ip, 10, 300)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Too many login attempts. Please try again later."));
        }


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

        if (!rateLimiterService.isAllowed("auth-register:ip:" + ip, 5, 3600)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Too many registration attempts. Please try again later."));
        }

        Locale locale = org.springframework.web.servlet.support.RequestContextUtils.getLocale(httpRequest);

        authService.register(request, locale);

        try {
            authService.sendWelcomeEmail(request.email(), request.firstName(), locale);
        } catch (Exception e) {
            log.error("No se pudo enviar el email de bienvenida a {}", request.email(), e);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestBody Map<String, String> body) {
        try {
            authService.verifyEmail(body.get("token"));
            return ResponseEntity.ok("Email verified successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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

        if (!rateLimiterService.isAllowed("auth-forgot:ip:" + ip, 3, 900)
                || !rateLimiterService.isAllowed("auth-forgot:email:" + request.email(), 3, 900)) {

            return ResponseEntity.ok("If the email exists, you will receive a recovery message.");
        }

        Locale locale = org.springframework.web.servlet.support.RequestContextUtils.getLocale(httpRequest);

        try {
            authService.initiatePasswordReset(request.email(), locale);
        } catch (Exception e) {
            log.error("Error initiating password reset for {}", request.email(), e);
        }

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
