package com.alexandracoder.littleneighbors.event.service;

import com.alexandracoder.littleneighbors.event.dto.EventMapper;
import com.alexandracoder.littleneighbors.event.dto.EventRequestDTO;
import com.alexandracoder.littleneighbors.event.dto.EventResponseDTO;
import com.alexandracoder.littleneighbors.event.entity.EventEntity;
import com.alexandracoder.littleneighbors.event.repository.EventRepository;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import com.alexandracoder.littleneighbors.neighborhood.repository.NeighborhoodRepository;
import com.alexandracoder.littleneighbors.specifications.EventSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventResponseDTO createEvent(EventRequestDTO requestDTO) {
        // 1. Buscamos el barrio en SU propio repositorio usando el nombre
        NeighborhoodEntity neighborhood = neighborhoodRepository.findByName(requestDTO.neighborhoodName())
                .orElseThrow(() -> new EntityNotFoundException("Barrio no encontrado: " + requestDTO.neighborhoodName()));

        // 2. Mapeamos el DTO a la entidad (usando tu mapper)
        EventEntity event = eventMapper.toEntity(requestDTO);

        // 3. ASIGNAMOS el objeto completo, no solo un ID
        event.setNeighborhood(neighborhood);

        // 4. Guardamos el evento
        EventEntity savedEvent = eventRepository.save(event);

        return eventMapper.toResponse(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> getEventsInArea(Double minLat, Double maxLat, Double minLon, Double maxLon) {
        // Combinamos el filtro geográfico con el de fechas futuras
        Specification<EventEntity> spec = Specification.where(EventSpecifications.withinBoundingBox(minLat, maxLat, minLon, maxLon))
                .and(EventSpecifications.isUpcoming());

        return eventRepository.findAll(spec).stream()
                .map(eventMapper::toResponse)
                .toList();
    }
}