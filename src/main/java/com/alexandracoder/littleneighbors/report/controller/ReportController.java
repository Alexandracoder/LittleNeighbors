package com.alexandracoder.littleneighbors.report.controller;

import com.alexandracoder.littleneighbors.report.dto.AdminReportDTO;
import com.alexandracoder.littleneighbors.report.dto.CreateReportRequestDTO;
import com.alexandracoder.littleneighbors.report.dto.ResolveReportRequestDTO;
import com.alexandracoder.littleneighbors.report.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // Cualquier familia autenticada puede reportar un mensaje, un perfil o
    // un evento. Está bajo /api/reports (no /api/admin), así que basta con
    // estar autenticado (regla ya cubierta por anyRequest().authenticated()).
    @PostMapping("/api/reports")
    public ResponseEntity<Void> createReport(
            @Valid @RequestBody CreateReportRequestDTO dto,
            Principal principal) {
        reportService.createReport(principal.getName(), dto);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/api/admin/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminReportDTO>> listPendingReports() {
        return ResponseEntity.ok(reportService.listPendingReports());
    }

    @PostMapping("/api/admin/reports/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resolveReport(
            @PathVariable Long id,
            @Valid @RequestBody ResolveReportRequestDTO dto,
            Principal principal) {
        reportService.resolveReport(id, dto, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
