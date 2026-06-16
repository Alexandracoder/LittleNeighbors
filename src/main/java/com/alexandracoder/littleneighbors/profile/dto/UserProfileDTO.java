package com.alexandracoder.littleneighbors.profile.dto;

import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;


import java.util.List;

public record UserProfileDTO(
        String email,
        List<String> roles,
        FamilyResponseDTO family
) {
}
