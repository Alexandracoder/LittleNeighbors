package com.alexandracoder.littleneighbors.qr.service;

import com.alexandracoder.littleneighbors.qr.entity.QrEntity;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

public interface QrService {
    QrEntity saveLead(String email, String neighborhood);
    List<QrEntity> findLeadsByCriteria(Specification<QrEntity> spec);
    long countLeadsByNeighborhood(String neighborhood);
}