package com.alexandracoder.littleneighbors.qr.service;

import com.alexandracoder.littleneighbors.qr.entity.QrEntity;
import com.alexandracoder.littleneighbors.qr.repository.QrRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QrServiceImpl implements QrService {

    private final QrRepository qrRepository;

    @Override
    @Transactional
    public QrEntity saveLead(String email, String neighborhood) {
        if (qrRepository.existsByEmailAndNeighborhood(email, neighborhood)) {
            throw new IllegalArgumentException("This family has already voted for this neighborhood.");
        }

        QrEntity lead = QrEntity.builder()
                .email(email.trim().toLowerCase())
                .neighborhood(neighborhood.trim())
                .build();

        return qrRepository.save(lead);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QrEntity> findLeadsByCriteria(Specification<QrEntity> spec) {
        return qrRepository.findAll(spec);
    }

    @Override
    public long countLeadsByNeighborhood(String neighborhood) {
        if (neighborhood == null || neighborhood.isBlank()) {
            return 0;
        }
        return qrRepository.countByNeighborhood(neighborhood);
    }
}