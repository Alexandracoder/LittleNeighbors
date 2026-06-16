package com.alexandracoder.littleneighbors.child.dto;

import com.alexandracoder.littleneighbors.enums.Gender;
import com.alexandracoder.littleneighbors.enums.LifeStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

public record ChildRequestDTO(
        @NotBlank(message = "Nickname is required")
        String nickname,

        LocalDate birthDate,
        LocalDate dueDate,

        @NotNull(message = "Life stage is required")
        LifeStage lifeStage,

        @NotNull(message = "Gender is required")
        Gender gender,

        Set<Long> interestIds,
        Boolean isPrenatal,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description

) {
    public ChildRequestDTO {
        interestIds = (interestIds == null) ? Set.of() : interestIds;
        isPrenatal = (isPrenatal == null) ? false : isPrenatal;

        if (lifeStage == LifeStage.BORN) {
            if (birthDate == null) {
                throw new IllegalArgumentException("Birth date is required for born children");
            }
            if (gender == null) {
                throw new IllegalArgumentException("Gender is required for born children");
            }
        }
    }
}