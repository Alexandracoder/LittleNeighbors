package com.alexandracoder.littleneighbors.qr.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.alexandracoder.littleneighbors.qr.PilotBarrios;
import com.alexandracoder.littleneighbors.qr.service.QrService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final QrService qrService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Long> getNeighborhoodStats() {
        return qrService.getAllNeighborhoodStats(PilotBarrios.BARRIOS);
    }

    @GetMapping("/stats/detailed")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, QrService.StatsDTO> getDetailedNeighborhoodStats() {
        return qrService.getDetailedNeighborhoodStats(PilotBarrios.BARRIOS);
    }
}