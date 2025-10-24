package com.alexandracoder.littleneighbors.child.service;

import com.alexandracoder.littleneighbors.child.dto.ChildRequestDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildResponseDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.child.mapper.ChildMapper;
import com.alexandracoder.littleneighbors.child.repository.ChildRepository;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.interest.entity.InterestEntity;
import com.alexandracoder.littleneighbors.interest.repository.InterestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ChildServiceImpl implements ChildService {

    private final ChildRepository childRepository;
    private final FamilyRepository familyRepository;
    private final InterestRepository interestRepository;
    private final ChildMapper childMapper;

    @Override
    public ChildResponseDTO create(ChildRequestDTO dto) {
        return saveChild(dto, null);
    }

    @Override
    public ChildResponseDTO update(Long id, ChildRequestDTO dto) {
        return saveChild(dto, id);
    }

    @Override
    public void delete(Long id) {
        ChildEntity child = childRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Child not found"));
        childRepository.delete(child);
    }

    @Override
    public ChildResponseDTO getById(Long id) {
        ChildEntity child = childRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Child not found with id: " + id));
        return childMapper.toResponseDTO(child);
    }
    @Override
    public List<ChildSummaryDTO> getAllSummaries() {
        return childRepository.findAll()
                .stream()
                .map(childMapper::toSummaryDTO)
                .toList();
    }

    private ChildResponseDTO saveChild(ChildRequestDTO dto, Long childId) {
        ChildEntity child;

        if (childId != null) {
            child = childRepository.findById(childId)
                    .orElseThrow(() -> new EntityNotFoundException("Child not found"));
        } else {
            child = childMapper.toEntity(dto);
        }

        FamilyEntity family = familyRepository.findById(dto.familyId())
                .orElseThrow(() -> new EntityNotFoundException("Family not found"));
        child.setFamily(family);

        if (dto.interestIds() != null && !dto.interestIds().isEmpty()) {
            Set<InterestEntity> interests = new HashSet<>(interestRepository.findAllById(dto.interestIds()));
            child.setInterests(interests);
        }

        return childMapper.toResponseDTO(childRepository.save(child));
    }
}