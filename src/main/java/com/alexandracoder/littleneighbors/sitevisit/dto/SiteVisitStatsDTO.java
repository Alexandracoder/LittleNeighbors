package com.alexandracoder.littleneighbors.sitevisit.dto;

import java.util.List;

public record SiteVisitStatsDTO(
        long totalVisits,
        long uniqueVisitors,
        long uniqueVisitorsLast30Days,
        List<DailyVisitsDTO> last30Days
) {
}
