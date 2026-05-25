package com.alexandracoder.littleneighbors.family.dto;

public record OnboardingResponseDTO(
        FamilyResponseDTO family,
        String accessToken,
        String refreshToken
) {
}
