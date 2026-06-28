package com.alexandracoder.littleneighbors.qr.service;

import com.alexandracoder.littleneighbors.qr.entity.QrEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QrService {
    QrEntity saveLead(String email, String neighborhood, boolean consentGiven, String privacyPolicyVersion);

    @Transactional(readOnly = true)
    Optional<QrEntity> findByInviteToken(String token);

    List<QrEntity> findLeadsByCriteria(Specification<QrEntity> spec);
    long countLeadsByNeighborhood(String neighborhood);

    @Transactional(readOnly = true)
    Map<String, Long> getAllNeighborhoodStats(List<String> neighborhoodList);

    @Transactional(readOnly = true)
    Map<String, StatsDTO> getDetailedNeighborhoodStats(List<String> neighborhoodList);

    public record StatsDTO(long totalLeads, long convertedLeads) {}
}