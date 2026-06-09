package com.alexandracoder.littleneighbors.qr.controller;

import com.alexandracoder.littleneighbors.qr.dto.PilotLeadRequest;
import com.alexandracoder.littleneighbors.qr.entity.QrEntity;
import com.alexandracoder.littleneighbors.qr.service.QrService;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> registerQrLead(@Valid @RequestBody PilotLeadRequest request) {

        try {
            QrEntity savedLead = qrService.saveLead(request.getEmail(), request.getNeighborhood());
            log.info("QR lead registered -> Neighborhood: {}, Email: {}",
                    savedLead.getNeighborhood(), savedLead.getEmail());

            return ResponseEntity.ok(Map.of(
                    "message", "Lead registered successfully.",
                    "inviteToken", savedLead.getInviteToken()
            ));
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

        String normalized = neighborhood.toLowerCase().replace(" (finca roja)", "").replace("el ", "");

        try {

            long count = qrService.countLeadsByNeighborhood(normalized);

            return ResponseEntity.ok(Map.of(
                    "neighborhood", neighborhood,
                    "count", count
            ));

        } catch (Exception e) {
            log.error("Error retrieving lead count for neighborhood: {}", neighborhood, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }

    @GetMapping("/invite/{token}")
    public ResponseEntity<?> getInviteDetails(@PathVariable String token) {
        return qrService.findByInviteToken(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}