package com.alexandracoder.littleneighbors.qr.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.alexandracoder.littleneighbors.qr.service.QrService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final QrService qrService;

    private static final List<String> BARRIOS_PILOTO = List.of(
            "Benimaclet", "Ruzafa", "Arrancapins", "Cabañal", "Velluters"
    );

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Long> getNeighborhoodStats() {
        return qrService.getAllNeighborhoodStats(BARRIOS_PILOTO);
    }
}