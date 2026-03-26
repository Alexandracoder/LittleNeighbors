package com.alexandracoder.littleneighbors.child.dto;

import com.alexandracoder.littleneighbors.enums.Gender;
import com.alexandracoder.littleneighbors.enums.LifeStage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

public record ChildRequestDTO(
        @JsonProperty("birthDate")
        LocalDate birthDate,

        @JsonProperty("dueDate")
        LocalDate dueDate,

        @JsonProperty("lifeStage")
        @NotNull(message = "El estado de vida es obligatorio")
        LifeStage lifeStage,

        @JsonProperty("gender")
        Gender gender,

        @JsonProperty("interestIds")
        Set<Long> interestIds,

        @JsonProperty("isPrenatal")
        Boolean isPrenatal
) {

    public ChildRequestDTO {
        if (interestIds == null) interestIds = Set.of();
        if (isPrenatal == null) isPrenatal = false;
    }
}