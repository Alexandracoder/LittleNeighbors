package com.alexandracoder.littleneighbors.user.dto;

import com.alexandracoder.littleneighbors.enums.VerificationStatus;
import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;

import java.util.List;
public record UserProfileResponseDTO(
        String email,
        List<String> roles,
        FamilyResponseDTO family,
        VerificationStatus verificationStatus
) {}
