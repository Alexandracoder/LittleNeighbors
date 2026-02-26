package com.alexandracoder.littleneighbors.user.dto;

public record UserResponseDTO(
        Long id,
        String email,
        String firstName,
        String lastName
) {}