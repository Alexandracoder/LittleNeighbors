package com.alexandracoder.littleneighbors.event.service;

import com.alexandracoder.littleneighbors.event.dto.EventRequestDTO;
import com.alexandracoder.littleneighbors.event.dto.EventResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EventService {
    List<EventResponseDTO> getEventsInArea(Double minLat, Double maxLat, Double minLong, Double maxLon);
    EventResponseDTO createEvent(EventRequestDTO requestDTO);

    void deleteEvent(Long id);
    EventResponseDTO getEventById(Long id);
    EventResponseDTO updateEvent(Long id, EventRequestDTO requestDTO);
}