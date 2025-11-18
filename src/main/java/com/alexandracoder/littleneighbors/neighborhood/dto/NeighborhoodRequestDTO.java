package com.alexandracoder.littleneighbors.neighborhood.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NeighborhoodRequestDTO(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 500) String description
) {}
