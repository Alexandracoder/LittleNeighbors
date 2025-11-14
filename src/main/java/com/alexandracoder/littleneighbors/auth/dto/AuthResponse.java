package com.alexandracoder.littleneighbors.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}
