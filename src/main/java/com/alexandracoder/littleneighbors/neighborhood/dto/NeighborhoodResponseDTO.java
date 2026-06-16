package com.alexandracoder.littleneighbors.neighborhood.dto;

public record NeighborhoodResponseDTO(
        Long id,
        String name,
        String streetName,
        String postalCode,
        Long cityId,
        String cityName
) {}
