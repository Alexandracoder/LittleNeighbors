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
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('FAMILY') or hasRole('ADMIN')")
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications(
            @PathVariable Long familyId,
            @RequestParam(required = false) Boolean unreadOnly,
            Principal principal) {

        // BUG DE SEGURIDAD: antes cualquier usuario autenticado podía leer
        // las notificaciones de CUALQUIER familia con solo cambiar el ID
        // en la URL (incluye nombres reales de otras familias/vecinos).
        List<NotificationResponseDTO> notifications =
                notificationService.getNotificationsForFamily(familyId, unreadOnly, principal.getName());
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{id}/read")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('FAMILY') or hasRole('ADMIN')")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Principal principal) {
        // BUG DE SEGURIDAD: antes cualquiera podía marcar como leída
        // cualquier notificación de cualquier familia con solo probar IDs.
        notificationService.markAsRead(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponseDTO>> getMyNotifications(Principal principal) {
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsByUserEmail(principal.getName());
        return ResponseEntity.ok(notifications);
    }
}