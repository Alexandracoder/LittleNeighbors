package com.alexandracoder.littleneighbors.child.dto;

import com.alexandracoder.littleneighbors.enums.Gender;
import com.alexandracoder.littleneighbors.enums.LifeStage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

public record ChildRequestDTO(
        @JsonProperty String nickname,
        @JsonProperty("birthDate") LocalDate birthDate,
        @JsonProperty("dueDate") LocalDate dueDate,

        @NotNull(message = "Life stage is required")
        @JsonProperty("lifeStage") LifeStage lifeStage,

        @JsonProperty("gender") Gender gender,

        @JsonProperty("interestIds") Set<Long> interestIds,

        @JsonProperty("isPrenatal") Boolean isPrenatal,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        @JsonProperty("description") String description

) {
    public ChildRequestDTO {

        if (interestIds == null) interestIds = Set.of();
        if (isPrenatal == null) isPrenatal = false;


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