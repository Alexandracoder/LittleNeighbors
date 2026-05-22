package com.alexandracoder.littleneighbors.dashboard.service;

import com.alexandracoder.littleneighbors.dashboard.dto.DashboardImpactDTO;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.playdate.repository.PlaydateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    private final FamilyRepository familyRepository;
    private final MatchRepository matchRepository;
    private final PlaydateRepository playdateRepository;


    public DashboardServiceImpl(FamilyRepository familyRepository,
                                MatchRepository matchRepository,
                                PlaydateRepository playdateRepository) {
        this.familyRepository = familyRepository;
        this.matchRepository = matchRepository;
        this.playdateRepository = playdateRepository;
    }

    @Override
    public DashboardImpactDTO getImpactStatsForUser(Long userId) {

        Specification<FamilyEntity> familySpec = Specification.where(null);
        long activeFamilies = familyRepository.count(familySpec);

        Specification<MatchEntity> matchSpec = Specification.where(null);
        long consolidatedPlaydates = matchRepository.count(matchSpec);

        long totalConciliationMinutes = 0;

        try {

            long totalMeetings = playdateRepository.countByStatusNotNull();

            totalConciliationMinutes = totalMeetings * 120L;

        } catch (Exception e) {

            log.error("Error crítico al calcular los minutos de conciliación para el usuario con ID: {}", userId, e);
            totalConciliationMinutes = 0L;
        }

        return new DashboardImpactDTO(activeFamilies, consolidatedPlaydates, totalConciliationMinutes);
    }
}