package com.alexandracoder.littleneighbors.event.dto;

import com.alexandracoder.littleneighbors.event.entity.EventEntity;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventResponseDTO toResponse(EventEntity entity) {
        return new EventResponseDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getEventDate(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getNeighborhood() != null ? entity.getNeighborhood().getId() : null
        );
    }

    public EventEntity toEntity(EventRequestDTO requestDTO) {
        return EventEntity.builder()
                .title(requestDTO.title())
                .description(requestDTO.description())
                .eventDate(requestDTO.eventDate())
                .latitude(requestDTO.latitude())
                .longitude(requestDTO.longitude())
                // No asignamos el barrio aquí, lo hacemos en el Service
                .build();
    }
}