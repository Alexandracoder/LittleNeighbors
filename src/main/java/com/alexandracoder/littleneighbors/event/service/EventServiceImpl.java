package com.alexandracoder.littleneighbors.event.service;

import com.alexandracoder.littleneighbors.event.dto.EventMapper;
import com.alexandracoder.littleneighbors.event.dto.EventRequestDTO;
import com.alexandracoder.littleneighbors.event.dto.EventResponseDTO;
import com.alexandracoder.littleneighbors.event.entity.EventEntity;
import com.alexandracoder.littleneighbors.event.repository.EventRepository;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.enums.NotificationType;
import com.alexandracoder.littleneighbors.notification.service.NotificationService;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import com.alexandracoder.littleneighbors.neighborhood.repository.NeighborhoodRepository;
import com.alexandracoder.littleneighbors.specifications.EventSpecifications;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final FamilyRepository familyRepository;
    private final NotificationService notificationService;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventResponseDTO createEvent(EventRequestDTO requestDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        FamilyEntity creator = familyRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Family profile not found"));

        NeighborhoodEntity neighborhood = neighborhoodRepository.findById(requestDTO.neighborhoodId())
                .orElseThrow(() -> new ResourceNotFoundException("Neighborhood not found"));

        EventEntity event = eventMapper.toEntity(requestDTO);
        event.setNeighborhood(neighborhood);
        event.setCreatorFamily(creator);

        EventEntity savedEvent = eventRepository.save(event);

        List<FamilyEntity> neighbors = familyRepository.findByNeighborhood_NameAndIdNot(
                neighborhood.getName(),
                creator.getId()
        );

        neighbors.forEach(neighbor -> {
            notificationService.createInternalNotification(
                    neighbor,
                    "New plan in your neighborhood!",
                    creator.getFamilyName() + " has organized: " + savedEvent.getTitle(),
                    NotificationType.EVENT_CREATED,
                    savedEvent.getId()
            );
        });

        return eventMapper.toResponse(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> getEventsInArea(Double minLat, Double maxLat, Double minLon, Double maxLon) {
        Specification<EventEntity> spec = Specification.where(EventSpecifications.withinBoundingBox(minLat, maxLat, minLon, maxLon))
                .and(EventSpecifications.isUpcoming());

        return eventRepository.findAll(spec).stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!event.getCreatorFamily().getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("You do not have permission to delete this event.");
        }

        eventRepository.delete(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDTO getEventById(Long id) {
        return eventRepository.findById(id)
                .map(eventMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    @Override
    @Transactional
    public EventResponseDTO updateEvent(Long id, EventRequestDTO requestDTO) {
        EventEntity existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!existingEvent.getCreatorFamily().getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("You do not have permission to edit this event.");
        }

        NeighborhoodEntity neighborhood = neighborhoodRepository.findById(requestDTO.neighborhoodId())
                .orElseThrow(() -> new ResourceNotFoundException("Neighborhood not found"));

        existingEvent.setTitle(requestDTO.title());
        existingEvent.setDescription(requestDTO.description());
        existingEvent.setEventDate(requestDTO.eventDate());
        existingEvent.setLatitude(requestDTO.latitude());
        existingEvent.setLongitude(requestDTO.longitude());
        existingEvent.setNeighborhood(neighborhood);

        return eventMapper.toResponse(eventRepository.save(existingEvent));
    }
}