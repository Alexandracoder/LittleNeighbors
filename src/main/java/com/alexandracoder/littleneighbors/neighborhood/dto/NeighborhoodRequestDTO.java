package com.alexandracoder.littleneighbors.neighborhood.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NeighborhoodRequestDTO(
        @NotBlank @Size(max = 255) String name,
        @NotBlank String streetName,
        @Size(max = 20) String postalCode,
        @NotNull Long cityId
) {}