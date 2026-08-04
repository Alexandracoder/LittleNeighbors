package com.alexandracoder.littleneighbors.event.dto;

import com.alexandracoder.littleneighbors.event.entity.EventEntity;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventResponseDTO toResponse(EventEntity entity, long attendeeCount, boolean isAttending) {
        return new EventResponseDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getEventDate(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getNeighborhood() != null ? entity.getNeighborhood().getId() : null,
                entity.getCreatorFamily() != null ? entity.getCreatorFamily().getId() : null,
                entity.getCreatorFamily() != null ? entity.getCreatorFamily().getFamilyName() : null,
                attendeeCount,
                isAttending
        );
    }

    // Para los casos en los que aún no tenemos el contexto de asistencia
    // (p.ej. justo tras crear el evento, cuando todavía nadie se ha
    // apuntado). Evita tener que pasar 0L/false a mano en cada sitio.
    public EventResponseDTO toResponse(EventEntity entity) {
        return toResponse(entity, 0L, false);
    }

    public EventEntity toEntity(EventRequestDTO requestDTO) {
        return EventEntity.builder()
                .title(requestDTO.title())
                .description(requestDTO.description())
                .eventDate(requestDTO.eventDate())
                .latitude(requestDTO.latitude())
                .longitude(requestDTO.longitude())
                .build();
    }
}
