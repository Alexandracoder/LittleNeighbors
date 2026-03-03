package com.alexandracoder.littleneighbors.family.dto;

public record FamilyAuthResponseDTO(
        FamilyResponseDTO family,
        String accessToken,
        String refreshToken
) {}