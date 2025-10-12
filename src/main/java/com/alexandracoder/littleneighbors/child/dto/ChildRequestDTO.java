package com.alexandracoder.littleneighbors.child.dto;

import com.alexandracoder.littleneighbors.enums.Gender;
import jakarta.validation.constraints.NotNull;

public record ChildRequestDTO(
        @NotNull Gender gender,
        @NotNull Long familyId
        ) {
}
