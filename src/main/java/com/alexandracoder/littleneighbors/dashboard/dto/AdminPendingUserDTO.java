package com.alexandracoder.littleneighbors.dashboard.dto;

import com.alexandracoder.littleneighbors.enums.VerificationStatus;

import java.time.LocalDateTime;

public record AdminPendingUserDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        VerificationStatus verificationStatus,
        LocalDateTime createdAt
) {}
