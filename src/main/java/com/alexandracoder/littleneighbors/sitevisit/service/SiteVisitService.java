package com.alexandracoder.littleneighbors.sitevisit.service;

import com.alexandracoder.littleneighbors.sitevisit.dto.DailyVisitsDTO;
import com.alexandracoder.littleneighbors.sitevisit.dto.SiteVisitStatsDTO;
import com.alexandracoder.littleneighbors.sitevisit.entity.SiteVisitEntity;
import com.alexandracoder.littleneighbors.sitevisit.repository.SiteVisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteVisitService {

    private final SiteVisitRepository siteVisitRepository;

    @Transactional
    public void recordVisit(String sessionId, String path) {
        SiteVisitEntity visit = SiteVisitEntity.builder()
                .sessionId(sessionId.trim())
                .path(path)
                .build();
        siteVisitRepository.save(visit);
    }

    @Transactional(readOnly = true)
    public SiteVisitStatsDTO getStats() {
        LocalDateTime since = LocalDateTime.now().minusDays(30);

        long totalVisits = siteVisitRepository.count();
        long uniqueVisitors = siteVisitRepository.countDistinctSessionId();
        long uniqueVisitorsLast30Days = siteVisitRepository.countDistinctSessionIdSince(since);

        List<DailyVisitsDTO> last30Days = siteVisitRepository.countDailyUniqueVisitorsSince(since).stream()
                .map(row -> new DailyVisitsDTO(row[0].toString(), (Long) row[1]))
                .toList();

        return new SiteVisitStatsDTO(totalVisits, uniqueVisitors, uniqueVisitorsLast30Days, last30Days);
    }
}
