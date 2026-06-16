package com.alexandracoder.littleneighbors.dashboard.controller;

import com.alexandracoder.littleneighbors.dashboard.service.ModerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/moderation")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderationService;

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
    @PostMapping("/reject/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectUser(@PathVariable Long userId, @RequestBody String reason) {
        moderationService.rejectUser(userId, reason);
        return ResponseEntity.ok().build();
    }
}