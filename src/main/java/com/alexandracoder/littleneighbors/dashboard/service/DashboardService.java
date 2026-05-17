package com.alexandracoder.littleneighbors.dashboard.service;

import com.alexandracoder.littleneighbors.dashboard.dto.DashboardImpactDTO;

public interface DashboardService {
    DashboardImpactDTO getImpactStatsForUser(Long userId);
}