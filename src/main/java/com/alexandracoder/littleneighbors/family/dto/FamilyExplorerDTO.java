package com.alexandracoder.littleneighbors.family.dto;

import java.util.List;

public record FamilyExplorerDTO(
        Long id,
        String familyName,
        String neighborhoodName,
        List<com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO> childStages,
        List<String> interests,
        String description,
        boolean isWeeklyQuotaFull
) {}
