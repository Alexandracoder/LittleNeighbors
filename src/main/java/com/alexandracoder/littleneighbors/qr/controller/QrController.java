package com.alexandracoder.littleneighbors.qr.controller;

import com.alexandracoder.littleneighbors.qr.entity.QrEntity;
import com.alexandracoder.littleneighbors.qr.service.QrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QrController {

    private final QrService qrService;

    @PostMapping("/pilot-lead")
    public ResponseEntity<?> registerQrLead(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String neighborhood = payload.get("neighborhood");

        if (email == null || neighborhood == null || email.isBlank() || neighborhood.isBlank()) {
            return ResponseEntity.badRequest().body("Email and neighborhood are required fields.");
        }

        try {
            QrEntity savedLead = qrService.saveLead(email, neighborhood);
            log.info(" QR lead successfully registered -> Neighborhood: {}, Email: {}",
                    savedLead.getNeighborhood(), savedLead.getEmail());

            return ResponseEntity.ok(Map.of("message", "Lead registered successfully."));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            log.error("Critical error processing QR registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }

    @GetMapping("/pilot-lead/count")
    public ResponseEntity<?> getNeighborhoodCount(@RequestParam String neighborhood) {
        if (neighborhood == null || neighborhood.isBlank()) {
            return ResponseEntity.badRequest().body("Neighborhood parameter is required.");
        }

        try {
            long count = qrService.countLeadsByNeighborhood(neighborhood);
            log.info("📊 Requesting lead count for neighborhood: {} -> Total: {}", neighborhood, count);

            return ResponseEntity.ok(Map.of(
                    "neighborhood", neighborhood,
                    "count", count
            ));

        } catch (Exception e) {
            log.error("Error retrieving lead count for neighborhood: {}", neighborhood, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }
}