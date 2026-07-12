package com.alexandracoder.littleneighbors.user.controller;

import com.alexandracoder.littleneighbors.user.dto.UserProfileDTO; // Importa tu DTO
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



    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserProfileDTO profile = profileService.getCurrentUserProfile(email);
        UserStatusDTO status = userService.getUserStatus(email);

        UserProfileDTO updatedProfile = new UserProfileDTO(
                profile.email(),
                profile.roles(),
                profile.family(),
                status.verificationStatus() // Aquí inyectas el valor
        );

        return ResponseEntity.ok(updatedProfile);
    }
    @GetMapping("/me/status")
    public ResponseEntity<UserStatusDTO> getUserStatus(Authentication authentication) {
        UserStatusDTO status = userService.getUserStatus(authentication.getName());
        return ResponseEntity.ok(status);
    }
}
