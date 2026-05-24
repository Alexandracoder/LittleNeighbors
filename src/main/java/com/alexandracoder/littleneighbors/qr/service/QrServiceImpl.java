package com.alexandracoder.littleneighbors.qr.service;

import com.alexandracoder.littleneighbors.qr.entity.QrEntity;
import com.alexandracoder.littleneighbors.qr.repository.QrRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
    public long countLeadsByNeighborhood(String neighborhood) {
        if (neighborhood == null || neighborhood.isBlank()) {
            return 0;
        }
        return qrRepository.countByNeighborhood(neighborhood.trim());
    }
}