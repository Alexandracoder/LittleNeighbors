package com.alexandracoder.littleneighbors.sitevisit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SiteVisitRequestDTO(
        @NotBlank @Size(max = 64) String sessionId,
        @Size(max = 255) String path
) {
}
