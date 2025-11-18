package com.alexandracoder.littleneighbors.family.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FamilyRequestDTO(
        String userId,
        @NotBlank @Size(max = 255) String representativeName,
        @NotBlank @Size(max = 255) String familyName,
        @NotBlank @Size(max = 1000) String description,
        String profilePictureUrl,
        @NotNull Long neighborhoodId
) {}


