package com.alexandracoder.littleneighbors.dashboard.controller;

import com.alexandracoder.littleneighbors.dashboard.dto.DashboardImpactDTO;
import com.alexandracoder.littleneighbors.dashboard.service.DashboardService;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
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
    public ResponseEntity<DashboardImpactDTO> getNeighborhoodImpact() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        Object principal = authentication.getPrincipal();
        String email = null;

        if (principal instanceof Jwt) {
            email = ((Jwt) principal).getSubject();
        } else if (principal instanceof UserEntity) {
            DashboardImpactDTO stats = dashboardService.getImpactStatsForUser(((UserEntity) principal).getId());
            return ResponseEntity.ok(stats);
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            email = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            email = (String) principal;
        }

        if (email != null) {
            final String finalEmail = email;

            UserEntity user = userRepository.findByEmail(finalEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + finalEmail));

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

        return ResponseEntity.status(403).build();
    }
}