package com.alexandracoder.littleneighbors.child.dto;

import com.alexandracoder.littleneighbors.enums.Gender;
import com.alexandracoder.littleneighbors.enums.LifeStage;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.Set;
import jakarta.validation.constraints.NotNull;

public record ChildRequestDTO(
        @JsonProperty("birthDate")LocalDate birthDate,
        @JsonProperty("lifeStage") @NotNull LifeStage lifeStage,
        @JsonProperty("gender") Gender gender,
        @JsonProperty("interestIds") @NotNull Set<Long> interestIds
) {}