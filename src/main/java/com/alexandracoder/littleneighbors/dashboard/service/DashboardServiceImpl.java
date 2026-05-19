package com.alexandracoder.littleneighbors.dashboard.service;

import com.alexandracoder.littleneighbors.dashboard.dto.DashboardImpactDTO;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.playdate.entity.PlaydateEntity;
import com.alexandracoder.littleneighbors.playdate.repository.PlaydateRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;

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

        // Contadores seguros para el MVP usando especificaciones base vacías
        Specification<FamilyEntity> familySpec = Specification.where(null);
        long activeFamilies = familyRepository.count(familySpec);

        Specification<MatchEntity> matchSpec = Specification.where(null);
        long consolidatedPlaydates = matchRepository.count(matchSpec);

        long totalConciliationMinutes = 0;
        try {
            List<PlaydateEntity> playdates = playdateRepository.findAll();
            if (playdates != null && !playdates.isEmpty()) {
                long totalMeetings = playdates.stream()
                        .filter(p -> p.getStatus() != null)
                        .count();

                totalConciliationMinutes = totalMeetings * 120L; // 2 horas por cita simulada
            }
        } catch (Exception e) {
            System.err.println("Error calculating playdate minutes: " + e.getMessage());
            totalConciliationMinutes = 0L;
        }

        return new DashboardImpactDTO(activeFamilies, consolidatedPlaydates, totalConciliationMinutes);
    }
}
