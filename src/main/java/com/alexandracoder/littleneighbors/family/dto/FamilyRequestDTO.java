package com.alexandracoder.littleneighbors.family.dto;

import com.alexandracoder.littleneighbors.enums.FamilyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record FamilyRequestDTO(
        String userId,
        @NotBlank @Size(max = 255) String representativeName,
        @NotBlank @Size(max = 255) String familyName,
        @NotBlank @Size(max = 1000) String description,
        @Pattern(
                regexp = "^$|^https://[\\w.-]+(:\\d+)?(/[^\\s]*)?$",
                message = "profilePictureUrl must be blank or a valid https:// URL"
        )
        @Size(max = 2048)
        String profilePictureUrl,
        @NotNull Long neighborhoodId,
        FamilyStatus status,
        List<String> familyInterests
) {}


