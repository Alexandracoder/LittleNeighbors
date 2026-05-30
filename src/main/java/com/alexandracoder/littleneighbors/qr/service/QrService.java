package com.alexandracoder.littleneighbors.qr.service;

import com.alexandracoder.littleneighbors.qr.entity.QrEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface QrService {
    QrEntity saveLead(String email, String neighborhood);
    List<QrEntity> findLeadsByCriteria(Specification<QrEntity> spec);
    long countLeadsByNeighborhood(String neighborhood);

    @Transactional(readOnly = true)
    Map<String, Long> getAllNeighborhoodStats(List<String> neighborhoodList);
}