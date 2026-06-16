package com.alexandracoder.littleneighbors.user.controller;

import com.alexandracoder.littleneighbors.profile.dto.UserProfileDTO; // Importa tu DTO
import com.alexandracoder.littleneighbors.profile.service.ProfileService; // Importa tu servicio
import com.alexandracoder.littleneighbors.user.dto.UserRegisterDTO;
import com.alexandracoder.littleneighbors.user.dto.UserResponseDTO;
import com.alexandracoder.littleneighbors.user.dto.UserStatusDTO;
import com.alexandracoder.littleneighbors.user.service.UserService;
import jakarta.validation.Valid;
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

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(
            @Valid @RequestBody UserRegisterDTO dto) {
        UserResponseDTO createdUser = userService.registerUser(dto);
        return ResponseEntity.ok(createdUser);
    }


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
