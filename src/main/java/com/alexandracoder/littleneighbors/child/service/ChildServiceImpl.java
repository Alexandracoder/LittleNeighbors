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

    // ADMIN
    @Override
    public List<ChildSummaryDTO> getAllSummaries() {
        return childRepository.findAll()
                .stream()
                .map(childMapper::toSummaryDTO)
                .toList();
    }

    @Override
    public ChildResponseDTO create(ChildRequestDTO dto, String username) {
        FamilyEntity family = getFamilyByUserEmail(username);

        ChildEntity child = new ChildEntity();
        child.setBirthDate(dto.birthDate());
        child.setGender(dto.gender());
        child.setFamily(family);

        if (dto.interestIds() != null && !dto.interestIds().isEmpty()) {
            Set<InterestEntity> interests = new HashSet<>(interestRepository.findAllById(dto.interestIds()));
            child.setInterests(interests);
        }

        ChildEntity saved = childRepository.save(child);
        return childMapper.toResponseDTO(saved);
    }

    // UPDATE
    @Override
    public ChildResponseDTO update(Long id, ChildRequestDTO dto, String username) {
        ChildEntity child = checkOwnership(id, username);

        child.setBirthDate(dto.birthDate());
        child.setGender(dto.gender());

        if (dto.interestIds() != null && !dto.interestIds().isEmpty()) {
            Set<InterestEntity> interests = new HashSet<>(interestRepository.findAllById(dto.interestIds()));
            child.setInterests(interests);
        }

        ChildEntity updated = childRepository.save(child);
        return childMapper.toResponseDTO(updated);
    }

    // DELETE
    @Override
    public void delete(Long id, String username) {
        ChildEntity child = checkOwnership(id, username);
        childRepository.delete(child);
    }

    // GET BY ID
    @Override
    public ChildResponseDTO getById(Long id, String username) {
        ChildEntity child = checkOwnership(id, username);
        return childMapper.toResponseDTO(child);
    }

    // AUX
    @Override
    public FamilyEntity getFamilyByUserEmail(String email) {
        return familyRepository.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Family not found for user email: " + email));
    }

    // Helper privado: válida existencia y propiedad
    private ChildEntity checkOwnership(Long childId, String username) {
        ChildEntity child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException("Child not found with id: " + childId));

        FamilyEntity family = child.getFamily();
        if (family == null || family.getUser() == null || family.getUser().getEmail() == null) {
            throw new SecurityException("Child has no associated family/user");
        }

        if (!family.getUser().getEmail().equals(username)) {
            throw new SecurityException("You do not have permission to access this child");
        }

        return child;
    }
}
