package com.alexandracoder.littleneighbors.notification.controller;

import com.alexandracoder.littleneighbors.notification.dto.NotificationResponseDTO;
import com.alexandracoder.littleneighbors.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/family/{familyId}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications(
            @PathVariable Long familyId,
            @RequestParam(required = false) Boolean unreadOnly) {

        List<NotificationResponseDTO> notifications = notificationService.getNotificationsForFamily(familyId, unreadOnly);
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponseDTO>> getMyNotifications(Principal principal) {
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsByUserEmail(principal.getName());
        return ResponseEntity.ok(notifications);
    }
}