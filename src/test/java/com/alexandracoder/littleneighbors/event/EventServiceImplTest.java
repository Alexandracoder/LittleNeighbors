package com.alexandracoder.littleneighbors.event;

import com.alexandracoder.littleneighbors.event.dto.EventMapper;
import com.alexandracoder.littleneighbors.event.dto.EventRequestDTO;
import com.alexandracoder.littleneighbors.event.dto.EventResponseDTO;
import com.alexandracoder.littleneighbors.event.entity.EventEntity;
import com.alexandracoder.littleneighbors.event.repository.EventRepository;
import com.alexandracoder.littleneighbors.event.service.EventServiceImpl;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import com.alexandracoder.littleneighbors.neighborhood.repository.NeighborhoodRepository;
import com.alexandracoder.littleneighbors.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock private EventRepository eventRepository;
    @Mock private NeighborhoodRepository neighborhoodRepository;
    @Mock private FamilyRepository familyRepository;
    @Mock private NotificationService notificationService;
    @Mock private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    private NeighborhoodEntity neighborhood;
    private EventEntity eventEntity;
    private FamilyEntity family;

    @BeforeEach
    void setUp() {
        neighborhood = NeighborhoodEntity.builder().id(1L).name("Mislata").build();
        family = FamilyEntity.builder().id(1L).familyName("Rojas").build();
        eventEntity = EventEntity.builder()
                .id(1L)
                .title("Test Event")
                .neighborhood(neighborhood)
                .build();
    }

    @Test
    void CreateEvent_Succes() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);

        EventRequestDTO request = new EventRequestDTO("Title", "Description", LocalDateTime.now(), 0.0, 0.0, 1L);

        when(familyRepository.findByUserEmail(anyString())).thenReturn(Optional.of(family));
        when(neighborhoodRepository.findById(1L)).thenReturn(Optional.of(neighborhood));
        when(eventMapper.toEntity(any(EventRequestDTO.class))).thenReturn(eventEntity);
        when(eventRepository.save(any(EventEntity.class))).thenReturn(eventEntity);
        when(familyRepository.findByNeighborhood_NameAndIdNot(anyString(), anyLong())).thenReturn(Collections.emptyList());
        when(eventMapper.toResponse(any(EventEntity.class)))
                .thenReturn(new EventResponseDTO(1L, "Title", "Desc", LocalDateTime.now(), 0.0, 0.0, 1L));

        EventResponseDTO response = eventService.createEvent(request);

        assertNotNull(response);
        assertEquals("Title", response.title());
        verify(eventRepository).save(any(EventEntity.class));
    }

    @Test
    void getEventsInArea_success() {
        List<EventEntity> events = List.of(eventEntity);

        when(eventRepository.findAll(any(Specification.class))).thenReturn(events);
        when(eventMapper.toResponse(any(EventEntity.class)))
                .thenReturn(new EventResponseDTO(1L, "Event 1", "Desc", LocalDateTime.now(), 1.0, 1.0, 1L));

        List<EventResponseDTO> results = eventService.getEventsInArea(0.0, 2.0, 0.0, 2.0);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        verify(eventRepository).findAll(any(Specification.class));
    }
}