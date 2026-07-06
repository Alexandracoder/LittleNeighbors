package com.alexandracoder.littleneighbors.user.controller;

import com.alexandracoder.littleneighbors.profile.dto.UserProfileDTO; // Importa tu DTO
import com.alexandracoder.littleneighbors.profile.service.ProfileService; // Importa tu servicio
import com.alexandracoder.littleneighbors.user.dto.UserStatusDTO;
import com.alexandracoder.littleneighbors.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProfileService profileService;

    // El registro de usuarios se hace exclusivamente por /api/auth/register
    // (AuthController), que sí tiene rate limiting y consentimiento RGPD.
    // Este controller solía tener también un POST /register duplicado sin
    // ninguna de esas protecciones; se eliminó a propósito.

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUser(Authentication authentication) {

        UserProfileDTO profile = profileService.getCurrentUserProfile(authentication.getName());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/me/status")
    public ResponseEntity<UserStatusDTO> getUserStatus(Authentication authentication) {
        UserStatusDTO status = userService.getUserStatus(authentication.getName());
        return ResponseEntity.ok(status);
    }
}
