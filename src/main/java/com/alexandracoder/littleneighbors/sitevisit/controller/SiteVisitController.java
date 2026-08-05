package com.alexandracoder.littleneighbors.sitevisit.controller;

import com.alexandracoder.littleneighbors.shared.ratelimit.RateLimiterService;
import com.alexandracoder.littleneighbors.sitevisit.dto.SiteVisitRequestDTO;
import com.alexandracoder.littleneighbors.sitevisit.dto.SiteVisitStatsDTO;
import com.alexandracoder.littleneighbors.sitevisit.service.SiteVisitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SiteVisitController {

    private final SiteVisitService siteVisitService;
    private final RateLimiterService rateLimiterService;

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
    }

    // Público a propósito (ver SecurityConfig: /api/public/** no requiere
    // autenticación), así se registra la visita incluso en /login o en la
    // landing, antes de que exista ningún usuario autenticado. sessionId
    // es un UUID anónimo generado en el navegador, no un dato personal.
    @PostMapping("/api/public/site-visits")
    public ResponseEntity<Void> recordVisit(
            @Valid @RequestBody SiteVisitRequestDTO request,
            HttpServletRequest httpRequest) {

        String ip = resolveClientIp(httpRequest);
        // 20 registros por IP cada 10 minutos: de sobra para uso normal
        // (una visita por sesión/día), pero corta de raíz un intento de
        // inflar el contador a base de peticiones repetidas.
        if (!rateLimiterService.isAllowed("site-visit:ip:" + ip, 20, 600)) {
            log.warn("Rate limit exceeded for site visit tracking. IP: {}", ip);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        siteVisitService.recordVisit(request.sessionId(), request.path());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/admin/stats/site-visits")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SiteVisitStatsDTO> getStats() {
        return ResponseEntity.ok(siteVisitService.getStats());
    }
}
