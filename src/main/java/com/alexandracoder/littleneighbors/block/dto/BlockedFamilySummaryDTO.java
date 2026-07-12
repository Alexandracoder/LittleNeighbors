package com.alexandracoder.littleneighbors.block.dto;

import java.time.LocalDateTime;

public record BlockedFamilySummaryDTO(
        Long blockedFamilyId,
        String blockedFamilyName,
        LocalDateTime blockedAt
) {}
