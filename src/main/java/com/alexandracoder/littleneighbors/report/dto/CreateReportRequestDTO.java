package com.alexandracoder.littleneighbors.report.dto;

import com.alexandracoder.littleneighbors.enums.ReportReason;
import com.alexandracoder.littleneighbors.enums.ReportType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReportRequestDTO(
        @NotNull ReportType reportType,
        // ID del mensaje/familia/evento reportado, según reportType.
        // Para PROFILE es el id de la familia reportada.
        Long relatedId,
        // Para MESSAGE/EVENT, de qué familia proviene el contenido (para
        // poder bloquear/verificar la cuenta correcta). Para PROFILE puede
        // omitirse si relatedId ya es el id de la familia.
        Long reportedFamilyId,
        @NotNull ReportReason reason,
        @Size(max = 1000) String description,
        @Size(max = 2000) String contentSnapshot
) {}
