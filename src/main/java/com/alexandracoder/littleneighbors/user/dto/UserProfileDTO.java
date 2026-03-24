package com.alexandracoder.littleneighbors.user.dto;

import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record UserProfileDTO(
        String email,
        List<String> roles,
        @Schema(hidden = true)
        FamilyResponseDTO family
) {
}
