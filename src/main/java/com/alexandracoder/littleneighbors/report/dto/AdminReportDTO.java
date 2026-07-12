package com.alexandracoder.littleneighbors.report.dto;

import com.alexandracoder.littleneighbors.enums.ReportReason;
import com.alexandracoder.littleneighbors.enums.ReportStatus;
import com.alexandracoder.littleneighbors.enums.ReportType;

import java.time.LocalDateTime;

public record AdminReportDTO(
        Long id,
        String reporterFamilyName,
        String reporterEmail,
        String reportedFamilyName,
        Long reportedFamilyId,
        Long reportedUserId,
        ReportType reportType,
        Long relatedId,
        ReportReason reason,
        String description,
        String contentSnapshot,
        ReportStatus status,
        LocalDateTime createdAt
) {}
