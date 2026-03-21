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
        NeighborhoodEntity neighborhood = neighborhoodRepository.findById(requestDTO.neighborhoodId())
                .orElseThrow(() -> new EntityNotFoundException("Barrio no encontrado: " + requestDTO.neighborhoodId()));

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

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("No se puede borrar: Evento no encontrado con ID " + id);
        }
        eventRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDTO getEventById(Long id) {
        return eventRepository.findById(id)
                .map(eventMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado con ID " + id));
    }

    @Override
    @Transactional
    public EventResponseDTO updateEvent(Long id, EventRequestDTO requestDTO) {
        // 1. Buscamos el evento existente (Si no existe, lanzamos error)
        EventEntity existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se puede actualizar: Evento no encontrado con ID " + id));

        // 2. Buscamos la entidad del Barrio (Crucial para que JPA no falle)
        NeighborhoodEntity neighborhood = neighborhoodRepository.findById(requestDTO.neighborhoodId())
                .orElseThrow(() -> new EntityNotFoundException("Barrio no encontrado con ID " + requestDTO.neighborhoodId()));

        // 3. Actualizamos los campos básicos
        existingEvent.setTitle(requestDTO.title());
        existingEvent.setDescription(requestDTO.description());
        existingEvent.setEventDate(requestDTO.eventDate());
        existingEvent.setLatitude(requestDTO.latitude());
        existingEvent.setLongitude(requestDTO.longitude());

        // 4. ASIGNAMOS LA ENTIDAD COMPLETA DEL BARRIO
        existingEvent.setNeighborhood(neighborhood);

        // 5. Guardamos y mapeamos a respuesta
        EventEntity savedEvent = eventRepository.save(existingEvent);
        return eventMapper.toResponse(savedEvent);
    }
}