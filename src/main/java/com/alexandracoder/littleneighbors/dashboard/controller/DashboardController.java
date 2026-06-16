package com.alexandracoder.littleneighbors.dashboard.controller;

import com.alexandracoder.littleneighbors.dashboard.dto.DashboardImpactDTO;
import com.alexandracoder.littleneighbors.dashboard.service.DashboardService;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    @Value("${app.demo-mode:false}")
    private boolean demoMode;

    public DashboardController(DashboardService dashboardService, UserRepository userRepository) {
        this.dashboardService = dashboardService;
        this.userRepository = userRepository;
    }

    @GetMapping("/impact-stats")
    public ResponseEntity<DashboardImpactDTO> getNeighborhoodImpact(Principal principal) {

        if (principal != null) {
            UserEntity user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found with email: " + principal.getName()));

            DashboardImpactDTO stats = dashboardService.getImpactStatsForUser(user.getId());
            return ResponseEntity.ok(stats);
        }

        if (demoMode) {
            Long mockId = userRepository.findAll().stream()
                    .findFirst()
                    .map(UserEntity::getId)
                    .orElse(1L);
            return ResponseEntity.ok(dashboardService.getImpactStatsForUser(mockId));
        }

        return ResponseEntity.status(401).build();
    }
}