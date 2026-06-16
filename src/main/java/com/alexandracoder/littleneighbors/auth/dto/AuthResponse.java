package com.alexandracoder.littleneighbors.auth.dto;

import java.util.List;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long id,
        String email,
        String firstName,
        String lastName,
        List<String> roles
) {}
