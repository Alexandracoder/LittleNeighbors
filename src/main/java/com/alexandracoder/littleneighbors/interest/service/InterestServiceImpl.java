package com.alexandracoder.littleneighbors.interest.service;

import com.alexandracoder.littleneighbors.interest.dto.InterestResponseDTO;
import com.alexandracoder.littleneighbors.interest.repository.InterestRepository;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService {

    private final InterestRepository interestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InterestResponseDTO> findAll() {
        return interestRepository.findAll().stream()
                .map(entity -> new InterestResponseDTO(
                        entity.getId(),
                        entity.getName(),
                        entity.getType(),
                        entity.getIcon()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InterestResponseDTO findById(Long id) {
        return interestRepository.findById(id)
                .map(entity -> new InterestResponseDTO(
                        entity.getId(),
                        entity.getName(),
                        entity.getType(),
                        entity.getIcon()
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Interest not found with ID: " + id));
    }
}