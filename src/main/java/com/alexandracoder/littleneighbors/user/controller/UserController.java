package com.alexandracoder.littleneighbors.user.controller;

import com.alexandracoder.littleneighbors.user.dto.UserRegisterDTO;
import com.alexandracoder.littleneighbors.user.dto.UserResponseDTO;
import com.alexandracoder.littleneighbors.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Registro de un nuevo usuario (público)
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(
            @Valid @RequestBody UserRegisterDTO dto) {

        UserResponseDTO createdUser = userService.registerUser(dto);
        return ResponseEntity.ok(createdUser);
    }
}
