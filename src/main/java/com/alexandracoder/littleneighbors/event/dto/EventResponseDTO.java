package com.alexandracoder.littleneighbors.event.dto;

import java.time.LocalDateTime;

public record EventResponseDTO(
        Long id,
        String title,
        String description,
        LocalDateTime eventDate,
        Double latitude,
        Double longitude,
        String neighborhoodName
) {
}
