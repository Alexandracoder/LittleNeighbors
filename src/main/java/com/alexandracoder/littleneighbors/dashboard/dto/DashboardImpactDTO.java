package com.alexandracoder.littleneighbors.dashboard.dto;

public record DashboardImpactDTO(
        long activeFamiliesCount,
        long consolidatedPlaydatesCount,
        long totalConciliationMinutes
) {}