package com.alexandracoder.littleneighbors.dashboard.controller;

import com.alexandracoder.littleneighbors.dashboard.service.ModerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/moderation")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderationService; // Inyecta la interfaz

    @PostMapping("/verify/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> verifyUser(@PathVariable Long userId) {
        moderationService.verifyUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/block/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> blockUser(@PathVariable Long userId) {
        moderationService.blockUser(userId);
        return ResponseEntity.ok().build();
    }
}