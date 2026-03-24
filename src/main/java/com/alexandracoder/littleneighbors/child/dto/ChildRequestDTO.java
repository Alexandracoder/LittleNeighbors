package com.alexandracoder.littleneighbors.child.dto;

import com.alexandracoder.littleneighbors.enums.Gender;
import com.alexandracoder.littleneighbors.enums.LifeStage;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

public record ChildRequestDTO(
        @JsonProperty("birthDate") LocalDate birthDate,
        @JsonProperty("dueDate") LocalDate dueDate,
        @JsonProperty("lifeStage") @NotNull LifeStage lifeStage,
        @JsonProperty("gender") Gender gender,
        @JsonProperty("interestIds") @NotNull Set<Long> interestIds,
        @JsonProperty("isPrenatal") Boolean isPrenatal
) {
    @JsonCreator
    public ChildRequestDTO(
            LocalDate birthDate,
            LocalDate dueDate,
            LifeStage lifeStage,
            Gender gender,
            Set<Long> interestIds,
            Boolean isPrenatal
    ) {
        this.birthDate = birthDate;
        this.dueDate = dueDate;
        this.lifeStage = lifeStage;
        this.gender = gender;
        this.interestIds = (interestIds == null) ? Set.of() : interestIds;
        this.isPrenatal = (isPrenatal != null && isPrenatal);
    }
}