package com.alexandracoder.littleneighbors.child.dto;

import com.alexandracoder.littleneighbors.enums.Gender;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

public record ChildRequestDTO(
        @NotNull LocalDate birthDate,
        @NotNull Gender gender,
        @NotNull Set<Long> interestIds
) {
}