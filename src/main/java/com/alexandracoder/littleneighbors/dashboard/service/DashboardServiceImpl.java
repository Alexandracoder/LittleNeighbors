package com.alexandracoder.littleneighbors.dashboard.service;

import com.alexandracoder.littleneighbors.dashboard.dto.DashboardImpactDTO;

import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;

import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.playdate.repository.PlaydateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final FamilyRepository familyRepository;
    private final MatchRepository matchRepository;
    private final PlaydateRepository playdateRepository;

    @Override
    public DashboardImpactDTO getImpactStatsForUser(Long userId) {
        return new DashboardImpactDTO(
                safeCount(familyRepository::count, "familias"),
                safeCount(matchRepository::count, "matches"),
                calculateConciliationImpact()
        );
    }

    private long safeCount(java.util.function.Supplier<Long> countFunction, String entityName) {
        try { return countFunction.get(); }
        catch (Exception e) { log.error("Error en {}: {}", entityName, e.getMessage()); return 0L; }
    }

    private long calculateConciliationImpact() {
        try { return playdateRepository.countByStatusNotNull() * 120L; }
        catch (Exception e) { log.error("Error conciliation impact: {}", e.getMessage()); return 0L; }
    }
}