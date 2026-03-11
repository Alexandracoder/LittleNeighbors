package com.alexandracoder.littleneighbors.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventRequestDTO(
        @NotBlank String title,
        String description,
        @NotNull LocalDateTime eventDate,
        @NotNull Double latitude,
        @NotNull Double longitude,
        String neighborhoodName
        ) {
}
