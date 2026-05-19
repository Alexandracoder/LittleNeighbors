package com.alexandracoder.littleneighbors.dashboard.controller;

import com.alexandracoder.littleneighbors.dashboard.dto.DashboardImpactDTO;
import com.alexandracoder.littleneighbors.dashboard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/impact-stats")
    public ResponseEntity<DashboardImpactDTO> getNeighborhoodImpact() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }


        System.out.println("--- DEBUG: Auth Principal type is: " + authentication.getPrincipal().getClass().getName());

        Long userId;
        try {

            if (authentication.getPrincipal() instanceof com.alexandracoder.littleneighbors.user.entity.UserEntity) {
                userId = ((com.alexandracoder.littleneighbors.user.entity.UserEntity) authentication.getPrincipal()).getId();
            }

            else if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {


                userId = 1L;
            } else {
                userId = 1L;
            }
        } catch (Exception e) {
            System.err.println("Failed to parse user ID from token context, falling back to mock ID: " + e.getMessage());
            userId = 1L;
        }

        DashboardImpactDTO stats = dashboardService.getImpactStatsForUser(userId);
        return ResponseEntity.ok(stats);
    }
}