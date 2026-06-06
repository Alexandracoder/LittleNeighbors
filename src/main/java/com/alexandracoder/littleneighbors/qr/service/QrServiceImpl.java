package com.alexandracoder.littleneighbors.qr.service;

import com.alexandracoder.littleneighbors.qr.entity.QrEntity;
import com.alexandracoder.littleneighbors.qr.repository.QrRepository;
import com.alexandracoder.littleneighbors.specifications.QrSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID; // Importante para el token

@Service
@RequiredArgsConstructor
public class QrServiceImpl implements QrService {

    private final QrRepository qrRepository;

    @Override
    @Transactional
    public QrEntity saveLead(String email, String neighborhood) {
        String normalizedEmail = email.trim().toLowerCase();
        String normalizedNeighborhood = neighborhood.trim();

        if (qrRepository.existsByEmailAndNeighborhood(normalizedEmail, normalizedNeighborhood)) {
            throw new IllegalArgumentException("¡Esta familia ya ha votado por este barrio! Gracias por tu entusiasmo. 🏘️");
        }

        QrEntity lead = QrEntity.builder()
                .email(normalizedEmail)
                .neighborhood(normalizedNeighborhood)
                .inviteToken(UUID.randomUUID().toString())
                .build();

        return qrRepository.save(lead);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<QrEntity> findByInviteToken(String token) {
        return qrRepository.findByInviteToken(token);
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

    @Transactional(readOnly = true)
    @Override
    public Map<String, StatsDTO> getDetailedNeighborhoodStats(List<String> neighborhoodList) {
        Map<String, StatsDTO> stats = new HashMap<>();
        for (String n : neighborhoodList) {
            long total = qrRepository.countByNeighborhood(n);
            long converted = qrRepository.countByNeighborhoodAndConvertedAtIsNotNull(n); // Necesitarás este método en el Repository
            stats.put(n, new StatsDTO(total, converted));
        }
        return stats;
    }
}