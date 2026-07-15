package com.alexandracoder.littleneighbors.event.service;

import com.alexandracoder.littleneighbors.event.dto.EventRequestDTO;
import com.alexandracoder.littleneighbors.event.dto.EventResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EventService {
    List<EventResponseDTO> getEventsInArea(Double minLat, Double maxLat, Double minLong, Double maxLon, String currentUserEmail, boolean citywide);
    EventResponseDTO createEvent(EventRequestDTO requestDTO);

    void deleteEvent(Long id);
    EventResponseDTO getEventById(Long id);
    EventResponseDTO updateEvent(Long id, EventRequestDTO requestDTO);

    // "Quitar de mi vista": oculta el evento SOLO para quien lo pide, sin
    // borrarlo para el resto. Pensado para eventos que no has creado tú
    // (para los tuyos ya existe deleteEvent, que sí lo borra de verdad).
    void hideEvent(Long eventId, String currentUserEmail);
}