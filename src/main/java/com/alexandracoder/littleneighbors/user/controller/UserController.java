package com.alexandracoder.littleneighbors.user.controller;

import com.alexandracoder.littleneighbors.user.dto.UserRegisterDTO;
import com.alexandracoder.littleneighbors.user.dto.UserResponseDTO;
import com.alexandracoder.littleneighbors.user.dto.UserStatusDTO;
import com.alexandracoder.littleneighbors.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(
            @Valid @RequestBody UserRegisterDTO dto) {

        UserResponseDTO createdUser = userService.registerUser(dto);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/me/status")
    public ResponseEntity<UserStatusDTO> getUserStatus(Authentication authentication) {
        UserStatusDTO status = userService.getUserStatus(authentication.getName());

        return ResponseEntity.ok(status);
    }
}
