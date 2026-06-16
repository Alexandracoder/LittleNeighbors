package com.alexandracoder.littleneighbors.dashboard.service;

import com.alexandracoder.littleneighbors.dashboard.dto.DashboardImpactDTO;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.playdate.repository.PlaydateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final FamilyRepository familyRepository;
    private final MatchRepository matchRepository;
    private final PlaydateRepository playdateRepository;

    @Override
    @Cacheable(value = "impactStats", key = "'global'")
    public DashboardImpactDTO getImpactStatsForUser(Long userId) {

        return new DashboardImpactDTO(
                familyRepository.count(),
                matchRepository.count(),
                playdateRepository.countByStatusNotNull() * 120L
        );
    }
}