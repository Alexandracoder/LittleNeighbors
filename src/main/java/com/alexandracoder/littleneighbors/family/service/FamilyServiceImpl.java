package com.alexandracoder.littleneighbors.family.service;

import com.alexandracoder.littleneighbors.family.dto.FamilyMapper;
import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FamilyServiceImpl implements FamilyService {

    private final FamilyRepository familyRepository;

    @Override
    @Transactional(readOnly = true)
    public FamilyResponseDTO getFamilyById(Long id) {
        FamilyEntity entity = familyRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Family not found with id: " + id));

        return FamilyMapper.toResponse(entity);
    }
}
