package com.alexandracoder.littleneighbors.notification.controller;

import com.alexandracoder.littleneighbors.notification.entity.NotificationEntity;
import com.alexandracoder.littleneighbors.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/family/{familyId}")
    public ResponseEntity<List<NotificationEntity>> getNotifications(
            @PathVariable Long familyId,
            @RequestParam(required = false) Boolean unreadOnly) {

        List<NotificationEntity> notifications = notificationService.getNotificationsForFamily(familyId, unreadOnly);
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<NotificationEntity>> getMyNotifications(Principal principal) {
        List<NotificationEntity> notifications = notificationService.getNotificationsByUserEmail(principal.getName());
        return ResponseEntity.ok(notifications);
    }
}