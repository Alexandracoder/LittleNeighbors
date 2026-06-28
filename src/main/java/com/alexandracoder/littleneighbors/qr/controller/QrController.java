package com.alexandracoder.littleneighbors.qr.controller;

import com.alexandracoder.littleneighbors.qr.dto.PilotLeadRequest;
import com.alexandracoder.littleneighbors.qr.entity.QrEntity;
import com.alexandracoder.littleneighbors.qr.service.QrService;
import com.alexandracoder.littleneighbors.shared.ratelimit.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final RateLimiterService rateLimiterService;

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
    }

    @PostMapping("/pilot-lead")
    public ResponseEntity<?> registerQrLead(
            @Valid @RequestBody PilotLeadRequest request,
            HttpServletRequest httpRequest) {

        String ip = resolveClientIp(httpRequest);

        if (!rateLimiterService.isAllowed("qr-lead:ip:" + ip, 5, 600)) {
            log.warn("Rate limit exceeded for QR lead registration. IP: {}", ip);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many requests. Please try again later.");
        }

        if (!rateLimiterService.isAllowed("qr-lead:email:" + request.getEmail(), 3, 3600)) {
            log.warn("Rate limit exceeded for QR lead by email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many requests for this email. Please try again later.");
        }

        try {
            QrEntity savedLead = qrService.saveLead(request.getEmail(), request.getNeighborhood(), request.isConsentGiven(), request.getPrivacyPolicyVersion());
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