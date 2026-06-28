package com.alexandracoder.littleneighbors.qr.service;

import com.alexandracoder.littleneighbors.qr.entity.QrEntity;
import com.alexandracoder.littleneighbors.qr.repository.QrRepository;
import com.alexandracoder.littleneighbors.specifications.QrSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrServiceImpl implements QrService {

    private final QrRepository qrRepository;

    @Override
    @Transactional
    public QrEntity saveLead(String email, String neighborhood, boolean consentGiven, String privacyPolicyVersion) {
        if (!consentGiven) {
            throw new IllegalArgumentException("Consent is required to register.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        String normalizedNeighborhood = neighborhood.trim().toLowerCase();

        QrEntity lead = QrEntity.builder()
                .email(normalizedEmail)
                .neighborhood(normalizedNeighborhood)
                .consentGiven(true)
                .consentAt(LocalDateTime.now())
                .privacyPolicyVersion(privacyPolicyVersion)
                .build();

        try {
            return qrRepository.save(lead);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("¡Esta familia ya ha votado por este barrio!");
        }
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
        if (neighborhood == null || neighborhood.isBlank()) return 0;
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
            long converted = qrRepository.countByNeighborhoodAndConvertedAtIsNotNull(n);
            stats.put(n, new StatsDTO(total, converted));
        }
        return stats;
    }


    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void anonymizeExpiredLeads() {
        LocalDateTime cutoff = LocalDateTime.now().minusMonths(12);
        List<QrEntity> expired = qrRepository
                .findByConvertedAtIsNullAndAnonymizedFalseAndCreatedAtBefore(cutoff);

        for (QrEntity lead : expired) {
            lead.setEmail(sha256(lead.getEmail()));
            lead.setAnonymized(true);
        }

        if (!expired.isEmpty()) {
            qrRepository.saveAll(expired);
            log.info("RGPD: anonimizados {} leads expirados", expired.size());
        }
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return "anon:" + HexFormat.of().formatHex(hash).substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            return "anon:unknown";
        }
    }
}