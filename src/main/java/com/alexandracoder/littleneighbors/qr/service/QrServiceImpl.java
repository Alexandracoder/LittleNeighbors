package com.alexandracoder.littleneighbors.qr.service;

import com.alexandracoder.littleneighbors.qr.entity.QrEntity;
import com.alexandracoder.littleneighbors.qr.repository.QrRepository;
import com.alexandracoder.littleneighbors.specifications.QrSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QrServiceImpl implements QrService {

    private final QrRepository qrRepository;

    @Override
    @Transactional
    public QrEntity saveLead(String email, String neighborhood) {
        try {
            QrEntity lead = new QrEntity();
            lead.setEmail(email.trim().toLowerCase());
            lead.setNeighborhood(neighborhood.trim());
            return qrRepository.save(lead);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("¡Esta familia ya ha votado por este barrio! Gracias por tu entusiasmo. 🏘️");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<QrEntity> findLeadsByCriteria(Specification<QrEntity> spec) {
        return qrRepository.findAll(spec);
    }

    @Override
    @Transactional(readOnly = true)
    public long countLeadsByNeighborhood(String neighborhood) {
        if (neighborhood == null || neighborhood.isBlank()) {
            return 0;
        }
        // Consistencia total: usamos las especificaciones para el conteo
        return qrRepository.count(QrSpecifications.hasNeighborhood(neighborhood));
    }

    @Transactional(readOnly = true)
    @Override
    public Map<String, Long> getAllNeighborhoodStats(List<String> neighborhoodList) {
        Map<String, Long> stats = new HashMap<>();
        for (String n : neighborhoodList) {
            stats.put(n, countLeadsByNeighborhood(n));
        }
        return stats;
    }
}