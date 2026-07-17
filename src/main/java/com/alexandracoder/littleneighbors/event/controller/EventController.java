package com.alexandracoder.littleneighbors.event.controller;

import com.alexandracoder.littleneighbors.event.dto.EventRequestDTO;
import com.alexandracoder.littleneighbors.event.dto.EventResponseDTO;
import com.alexandracoder.littleneighbors.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/map")
    public ResponseEntity<List<EventResponseDTO>> getEventsInArea(
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLon,
            @RequestParam Double maxLon,
            @RequestParam(required = false, defaultValue = "false") boolean citywide,
            java.security.Principal principal) {

        return ResponseEntity.ok(eventService.getEventsInArea(minLat, maxLat, minLon, maxLon, principal.getName(), citywide));
    }

    @PostMapping("/{id}/hide")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<Void> hideEvent(@PathVariable Long id, java.security.Principal principal) {
        eventService.hideEvent(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventRequestDTO request) {
        return new ResponseEntity<>(eventService.createEvent(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequestDTO request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }
}