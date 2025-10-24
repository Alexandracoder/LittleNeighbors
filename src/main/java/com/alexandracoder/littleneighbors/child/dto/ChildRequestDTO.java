package com.alexandracoder.littleneighbors.child.dto;

import com.alexandracoder.littleneighbors.enums.Gender;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record ChildRequestDTO(
        @NotNull Gender gender,
        @NotNull Long familyId,
        @NotNull Set<Long> interestIds
        ) {
}
